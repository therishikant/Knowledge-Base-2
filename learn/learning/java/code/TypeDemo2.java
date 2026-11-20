// Lesson 5, part 2 — inheritance, overriding vs overloading vs hiding, interfaces, composition.
// Compile: javac TypeDemo2.java    Run: java TypeDemo2
public class TypeDemo2 {

    // ---------- 1. An abstract base: shared state + shared rules + one blank to fill. ----------
    static abstract class Notification {
        protected final String to;                 // shared state lives in the parent

        Notification(String to) {
            System.out.println("   [parent ctor] Notification(" + to + ")");
            this.to = to;
        }

        // The blank a child MUST fill in.
        abstract String channel();

        // Shared behaviour, written once. Template method: fixed steps, one varying step.
        final String send(String body) {
            return "via " + channel() + " to " + to + ": " + body;
        }
    }

    static class EmailNotification extends Notification {
        EmailNotification(String to) {
            super(to);                              // parent runs first, always
            System.out.println("   [child ctor]  EmailNotification");
        }
        @Override String channel() { return "email"; }
    }

    static class SmsNotification extends Notification {
        SmsNotification(String to) { super(to); }
        @Override String channel() { return "sms"; }
    }

    // ---------- 2. Overriding (run time) vs hiding (compile time) ----------
    static class Parent {
        String name = "parent-field";               // FIELDS hide
        String whoAmI()        { return "Parent.whoAmI (instance -> overridden)"; }
        static String label()  { return "Parent.label (static -> hidden)"; }
    }

    static class Child extends Parent {
        String name = "child-field";                // hides, does not override
        @Override String whoAmI() { return "Child.whoAmI (instance -> overridden)"; }
        static String label()     { return "Child.label (static -> hidden)"; }
    }

    // ---------- 3. Overloading: same name, different parameters, picked by the compiler ----------
    static String describe(Object o) { return "describe(Object)"; }
    static String describe(String s) { return "describe(String)"; }

    // ---------- 4. Interfaces: a capability, implementable many times over ----------
    interface Auditable {
        String auditLine();
        default String auditLoud() { return auditLine().toUpperCase(); }   // Java 8 default method
    }
    interface Archivable {
        String archiveKey();
    }

    static class Invoice implements Auditable, Archivable {
        private final String number;
        Invoice(String number) { this.number = number; }
        @Override public String auditLine()  { return "invoice " + number; }
        @Override public String archiveKey() { return "INV/" + number; }
    }

    // ---------- 5. Composition: reuse by HOLDING, not by extending ----------
    static class Engine {
        private final int hp;
        Engine(int hp) { this.hp = hp; }
        String start() { return "engine(" + hp + "hp) running"; }
    }

    static class Car {
        private final Engine engine;                // has-a, not is-a
        Car(Engine engine) { this.engine = engine; }
        String drive() { return "car moves because " + engine.start(); }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. constructors chain parent-first ---");
        Notification n1 = new EmailNotification("asha@example.com");
        Notification n2 = new SmsNotification("+91-99999");

        System.out.println();
        System.out.println("--- 2. one loop, many behaviours (polymorphism) ---");
        for (Notification n : new Notification[]{ n1, n2 })
            System.out.println("   " + n.send("your order shipped"));

        System.out.println();
        System.out.println("--- 3. overriding uses the REAL type, hiding uses the DECLARED type ---");
        Parent p = new Child();                     // declared Parent, really a Child
        System.out.println("   p.whoAmI() -> " + p.whoAmI());
        System.out.println("   p.name     -> " + p.name);
        System.out.println("   ((Child) p).name -> " + ((Child) p).name);
        System.out.println("   Parent.label()   -> " + Parent.label());
        System.out.println("   Child.label()    -> " + Child.label());

        System.out.println();
        System.out.println("--- 4. overloading is decided before the program runs ---");
        Object asObject = "I am really a String";
        System.out.println("   describe(\"literal\")  -> " + describe("literal"));
        System.out.println("   describe(asObject)   -> " + describe(asObject));

        System.out.println();
        System.out.println("--- 5. one class, two capabilities ---");
        Invoice inv = new Invoice("2026-001");
        System.out.println("   audit   -> " + inv.auditLine());
        System.out.println("   loud    -> " + inv.auditLoud());
        System.out.println("   archive -> " + inv.archiveKey());
        System.out.println("   inv instanceof Auditable ? " + (inv instanceof Auditable));

        System.out.println();
        System.out.println("--- 6. composition ---");
        System.out.println("   " + new Car(new Engine(150)).drive());

        System.out.println();
        System.out.println("--- 7. a bad cast fails at RUN time, not compile time ---");
        Object box = Integer.valueOf(7);
        try {
            String bad = (String) box;
            System.out.println(bad);
        } catch (ClassCastException e) {
            System.out.println("   ClassCastException: " + e.getMessage());
        }
    }
}
