import java.util.*;
import java.util.function.*;

// Part 1 & 2 of the java8-functional cluster: lambdas, functional interfaces, method references.
public class LambdaDemo {

    @FunctionalInterface
    interface Greeter {           // exactly one abstract method => a lambda can implement it
        String greet(String name);
    }

    static class Person {
        final String name;
        final int age;
        Person(String name, int age) { this.name = name; this.age = age; }
        String getName() { return name; }
        int getAge() { return age; }
        public String toString() { return name + "(" + age + ")"; }
    }

    public static void main(String[] args) {
        // 1. the same behaviour, three ways
        Greeter anon = new Greeter() {
            public String greet(String n) { return "Hi " + n; }
        };
        Greeter lambda = n -> "Hi " + n;
        System.out.println("anon   : " + anon.greet("ann"));
        System.out.println("lambda : " + lambda.greet("ann"));

        // 2. the four core shapes
        Function<String, Integer> length = s -> s.length();
        Predicate<String> isLong = s -> s.length() > 3;
        Consumer<String> shout = s -> System.out.println("shout: " + s.toUpperCase());
        Supplier<String> now = () -> "a fresh value";
        System.out.println("Function  : " + length.apply("hello"));
        System.out.println("Predicate : " + isLong.test("hi"));
        shout.accept("bob");
        System.out.println("Supplier  : " + now.get());

        // 3. composition — build big behaviour out of small behaviour
        Function<Integer, Integer> twice = n -> n * 2;
        Function<Integer, Integer> plus3 = n -> n + 3;
        System.out.println("andThen(twice,plus3).apply(5) = " + twice.andThen(plus3).apply(5));
        System.out.println("compose(twice,plus3).apply(5) = " + twice.compose(plus3).apply(5));
        Predicate<String> notEmpty = s -> !s.isEmpty();
        System.out.println("and/negate = " + notEmpty.and(isLong).test("hello")
                + " / " + isLong.negate().test("hello"));

        // 4. the four kinds of method reference
        Function<String, Integer> parse  = Integer::parseInt;       // static
        Function<String, String>  upper  = String::toUpperCase;     // unbound instance
        String prefix = "ID-";
        Function<String, String> withPrefix = prefix::concat;       // bound instance
        Supplier<ArrayList<String>> maker = ArrayList::new;         // constructor
        System.out.println("static      : " + (parse.apply("41") + 1));
        System.out.println("unbound     : " + upper.apply("carol"));
        System.out.println("bound       : " + withPrefix.apply("77"));
        System.out.println("constructor : " + maker.get());

        // 5. a lambda captures values, and they must be effectively final
        int captured = 10;
        Supplier<Integer> readsCaptured = () -> captured + 1;
        System.out.println("captured    : " + readsCaptured.get());

        // 6. Comparator — the functional interface interviews ask about most
        List<Person> people = new ArrayList<>(Arrays.asList(
                new Person("carol", 30), new Person("ann", 30), new Person("bob", 25)));
        people.sort(Comparator.comparingInt(Person::getAge)
                              .thenComparing(Person::getName));
        System.out.println("sorted      : " + people);
        people.sort(Comparator.comparing(Person::getName).reversed());
        System.out.println("reversed    : " + people);
    }
}
