import java.lang.ref.WeakReference;

// Shows stack-vs-heap behaviour, references, and that GC follows reachability.
public class MemoryDemo {
    static class Node { int v; Node(int v){ this.v = v; } }

    public static void main(String[] args) throws Exception {
        // Two variables, ONE object on the heap. b is a copy of the arrow, not the object.
        Node a = new Node(1);
        Node b = a;
        b.v = 42;
        System.out.println("a.v = " + a.v);          // 42 — same object

        // Primitives copy the VALUE, so the original is untouched.
        int x = 10, y = x;
        y = 20;
        System.out.println("x = " + x);              // 10

        // A weak reference does NOT keep an object alive; GC may reclaim it.
        WeakReference<Node> weak = new WeakReference<>(new Node(7));
        System.out.println("before gc: reachable = " + (weak.get() != null));
        System.gc();                                 // a hint, but usually acts here
        Thread.sleep(100);
        System.out.println("after gc:  reachable = " + (weak.get() != null));
    }
}
