import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

// Parts 2 and 3 of the memory cluster: reachability, cycles, generations, and a real leak.
public class GcDemo {

    static class Node {
        int id;
        Node friend;            // used to build a cycle
        byte[] payload = new byte[1024]; // 1 KB so memory numbers are visible
        Node(int id) { this.id = id; }
    }

    // The classic Java "leak": a static collection that only ever grows.
    static final List<Node> LEAK = new ArrayList<Node>();

    static long usedMb() {
        Runtime r = Runtime.getRuntime();
        return (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. unreachable means collectable ---");
        WeakReference<Node> watch = new WeakReference<Node>(new Node(1));
        System.out.println("before gc: still there = " + (watch.get() != null));
        System.gc();
        Thread.sleep(100);
        System.out.println("after  gc: still there = " + (watch.get() != null));

        System.out.println();
        System.out.println("--- 2. a cycle is still garbage ---");
        Node x = new Node(2);
        Node y = new Node(3);
        x.friend = y;               // x -> y
        y.friend = x;               // y -> x  (they point at each other)
        WeakReference<Node> watchCycle = new WeakReference<Node>(x);
        x = null;                   // drop both arrows from the stack
        y = null;
        System.gc();
        Thread.sleep(100);
        System.out.println("cycle survived gc? " + (watchCycle.get() != null));

        System.out.println();
        System.out.println("--- 3. short-lived objects are cheap ---");
        long before = usedMb();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2_000_000; i++) {
            Node temp = new Node(i);   // dies immediately, never escapes the loop
            if (temp.id == -1) System.out.println("never");
        }
        long millis = System.currentTimeMillis() - start;
        System.gc();
        Thread.sleep(100);
        System.out.println("allocated 2,000,000 short-lived objects in " + millis + " ms");
        System.out.println("used before = " + before + " MB, used after gc = " + usedMb() + " MB");

        System.out.println();
        System.out.println("--- 4. a leak: reachable but useless ---");
        for (int i = 0; i < 100_000; i++) {
            LEAK.add(new Node(i));     // nobody ever removes these
        }
        System.gc();
        Thread.sleep(100);
        System.out.println("after gc, still holding " + LEAK.size() + " nodes, used = " + usedMb() + " MB");
        LEAK.clear();                  // drop the arrows and the memory comes back
        System.gc();
        Thread.sleep(100);
        System.out.println("after clear() + gc, used = " + usedMb() + " MB");

        System.out.println();
        System.out.println("--- 5. who is collecting, and how big is the heap? ---");
        System.out.println("max heap  = " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB");
        for (java.lang.management.GarbageCollectorMXBean gc :
                java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("collector: " + gc.getName()
                    + " | collections = " + gc.getCollectionCount()
                    + " | total pause time = " + gc.getCollectionTime() + " ms");
        }
    }
}
