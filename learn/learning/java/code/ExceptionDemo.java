// try-with-resources closes resources in REVERSE order, even when something throws.
public class ExceptionDemo {
    static class Resource implements AutoCloseable {
        final String name;
        Resource(String n) { name = n; System.out.println("open " + n); }
        void use() { System.out.println("use " + name); }
        @Override public void close() { System.out.println("close " + name); }
    }

    public static void main(String[] args) {
        try (Resource a = new Resource("A"); Resource b = new Resource("B")) {
            a.use();
            b.use();
            throw new RuntimeException("boom");
        } catch (RuntimeException e) {
            System.out.println("caught: " + e.getMessage());
        } finally {
            System.out.println("finally always runs");
        }
    }
}
