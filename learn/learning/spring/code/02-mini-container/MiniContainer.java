import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lesson 02 — a tiny IoC container that demonstrates the two phases
 * (bean definitions -> instances), the singleton cache, recursive dependency
 * resolution, cycle detection, and the post-processor hook.
 *
 * Deliberately Java 8 compatible:
 *   javac MiniContainer.java && java MiniContainer
 */
public class MiniContainer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MiniComponent { boolean prototype() default false; }

    // ------------------------------------------------------------ container

    static class Container {
        /** PHASE 1 — the recipes.  Spring: DefaultListableBeanFactory.beanDefinitionMap */
        private final Map<String, Class<?>> definitions = new LinkedHashMap<String, Class<?>>();
        /** PHASE 2 — the cooked singletons. Spring: DefaultSingletonBeanRegistry.singletonObjects */
        private final Map<String, Object> singletons = new HashMap<String, Object>();
        /** Cycle detection. Spring: singletonsCurrentlyInCreation */
        private final Set<String> inCreation = new LinkedHashSet<String>();

        void register(Class<?>... classes) {
            for (Class<?> c : classes) {
                if (!c.isAnnotationPresent(MiniComponent.class))
                    throw new IllegalArgumentException(c + " is not a @MiniComponent");
                definitions.put(name(c), c);           // keyed by NAME, like Spring
            }
        }

        private String name(Class<?> c) {              // "BookService" -> "bookService"
            String s = c.getSimpleName();
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }

        /** Eagerly build every singleton — Spring's finishBeanFactoryInitialization(). */
        void refresh() {
            List<Map.Entry<String, Class<?>>> snapshot =
                    new ArrayList<Map.Entry<String, Class<?>>>(definitions.entrySet());
            for (Map.Entry<String, Class<?>> e : snapshot) {
                if (!isPrototype(e.getValue())) getBean(e.getKey());
            }
        }

        <T> T getBean(Class<T> type) {                 // by TYPE = a search over the map
            List<String> hits = new ArrayList<String>();
            for (Map.Entry<String, Class<?>> e : definitions.entrySet())
                if (type.isAssignableFrom(e.getValue())) hits.add(e.getKey());

            if (hits.isEmpty())
                throw new IllegalStateException("No qualifying bean of type " + type.getSimpleName());
            if (hits.size() > 1)
                throw new IllegalStateException("expected single bean of type "
                        + type.getSimpleName() + ", found " + hits);
            return type.cast(getBean(hits.get(0)));
        }

        Object getBean(String n) {
            Object cached = singletons.get(n);                       // 1. singleton cache hit?
            if (cached != null) return cached;

            Class<?> type = definitions.get(n);
            if (type == null) throw new IllegalStateException("No bean named '" + n + "'");
            if (!inCreation.add(n))                                  // 2. already on the stack = cycle
                throw new IllegalStateException("Circular dependency: " + inCreation + " -> " + n);
            try {
                Constructor<?> ctor = type.getDeclaredConstructors()[0];
                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++)
                    args[i] = getBean(paramTypes[i]);                // 3. recursion == ordering

                ctor.setAccessible(true);
                Object bean = ctor.newInstance(args);                // 4. instantiate
                bean = postProcess(bean);                            // 5. the proxy hook
                if (!isPrototype(type)) singletons.put(n, bean);     // 6. cache ONLY singletons
                return bean;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to create bean '" + n + "'", e);
            } finally {
                inCreation.remove(n);
            }
        }

        private boolean isPrototype(Class<?> c) {
            return c.getAnnotation(MiniComponent.class).prototype();
        }

        /** Override to wrap beans — this is where @Transactional would be woven in. */
        protected Object postProcess(Object bean) { return bean; }
    }

    // ---------------------------------------------------------------- demo

    @MiniComponent
    static class BookRepository {
        BookRepository() { System.out.println("  built BookRepository"); }
        List<String> all() { return Arrays.asList("Dune"); }
    }

    @MiniComponent
    static class BookService {
        final BookRepository repo;
        BookService(BookRepository repo) {
            this.repo = repo;
            System.out.println("  built BookService");
        }
    }

    @MiniComponent(prototype = true)
    static class Ticket {
        final long id = System.nanoTime();
    }

    public static void main(String[] args) {
        Container ctx = new Container();
        // Registration order deliberately "wrong" — resolution order is derived, not declared.
        ctx.register(BookService.class, BookRepository.class, Ticket.class);
        ctx.refresh();

        System.out.println("same singleton? " + (ctx.getBean(BookService.class) == ctx.getBean(BookService.class)));
        System.out.println("same prototype? " + (ctx.getBean(Ticket.class) == ctx.getBean(Ticket.class)));
    }
}
