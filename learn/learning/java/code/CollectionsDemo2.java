import java.util.*;

// Part 1 demo: List / Set / Queue behaviour you can see with your own eyes. Java 8 compatible.
public class CollectionsDemo2 {

    public static void main(String[] args) {
        System.out.println("--- 1. List keeps order and keeps duplicates ---");
        List<String> list = new ArrayList<String>();
        list.add("tea"); list.add("coffee"); list.add("tea");
        System.out.println("list        = " + list);
        System.out.println("list.get(1) = " + list.get(1));
        System.out.println("size        = " + list.size());

        System.out.println();
        System.out.println("--- 2. Set throws duplicates away ---");
        Set<String> hashSet   = new HashSet<String>();
        Set<String> linkedSet = new LinkedHashSet<String>();
        Set<String> treeSet   = new TreeSet<String>();
        for (String s : new String[]{"tea", "coffee", "tea", "milk"}) {
            hashSet.add(s); linkedSet.add(s); treeSet.add(s);
        }
        System.out.println("HashSet       = " + hashSet);   // no promised order
        System.out.println("LinkedHashSet = " + linkedSet); // insertion order
        System.out.println("TreeSet       = " + treeSet);   // sorted order

        System.out.println();
        System.out.println("--- 3. ArrayList vs LinkedList: same answers, different cost ---");
        List<Integer> arrayList  = new ArrayList<Integer>();
        List<Integer> linkedList = new LinkedList<Integer>();
        for (int i = 0; i < 100000; i++) { arrayList.add(i); linkedList.add(i); }

        long t0 = System.nanoTime();
        long sumA = 0;
        for (int i = 0; i < arrayList.size(); i++) sumA += arrayList.get(i);
        long arrayMs = (System.nanoTime() - t0) / 1000000;

        t0 = System.nanoTime();
        long sumL = 0;
        for (int i = 0; i < linkedList.size(); i++) sumL += linkedList.get(i);
        long linkedMs = (System.nanoTime() - t0) / 1000000;

        System.out.println("same sum?                  " + (sumA == sumL));
        System.out.println("ArrayList  get(i) loop ms: " + arrayMs);
        System.out.println("LinkedList get(i) loop ms: " + linkedMs);

        System.out.println();
        System.out.println("--- 4. ArrayDeque is both a stack and a queue ---");
        Deque<String> stack = new ArrayDeque<String>();
        stack.push("a"); stack.push("b"); stack.push("c");
        System.out.println("stack pop order:  " + stack.pop() + ", " + stack.pop()); // last in, first out

        Queue<String> queue = new ArrayDeque<String>();
        queue.offer("a"); queue.offer("b"); queue.offer("c");
        System.out.println("queue poll order: " + queue.poll() + ", " + queue.poll()); // first in, first out

        System.out.println();
        System.out.println("--- 5. Removing while looping ---");
        List<String> drinks = new ArrayList<String>(Arrays.asList("tea", "coffee", "milk"));
        try {
            for (String d : drinks) {
                if (d.equals("tea")) drinks.remove(d); // the list changed under the loop
            }
            System.out.println("removing \"tea\"    -> no exception?! " + drinks);
        } catch (ConcurrentModificationException e) {
            System.out.println("removing \"tea\"    -> " + e.getClass().getSimpleName());
        }

        // Removing the second-to-last element hides the bug: the loop just stops early.
        drinks = new ArrayList<String>(Arrays.asList("tea", "coffee", "milk"));
        List<String> seen = new ArrayList<String>();
        try {
            for (String d : drinks) {
                seen.add(d);
                if (d.equals("coffee")) drinks.remove(d);
            }
            System.out.println("removing \"coffee\" -> no exception, but only saw " + seen);
        } catch (ConcurrentModificationException e) {
            System.out.println("removing \"coffee\" -> " + e.getClass().getSimpleName());
        }

        drinks = new ArrayList<String>(Arrays.asList("tea", "coffee", "milk"));
        Iterator<String> it = drinks.iterator();
        while (it.hasNext()) { if (it.next().equals("coffee")) it.remove(); } // the safe way
        System.out.println("iterator.remove() -> " + drinks);

        drinks = new ArrayList<String>(Arrays.asList("tea", "coffee", "milk"));
        drinks.removeIf(new java.util.function.Predicate<String>() {
            public boolean test(String d) { return d.equals("coffee"); }
        });
        System.out.println("removeIf()        -> " + drinks);

        System.out.println();
        System.out.println("--- 6. A read-only view cannot be changed ---");
        List<String> readOnly = Collections.unmodifiableList(drinks);
        try {
            readOnly.add("water");
        } catch (UnsupportedOperationException e) {
            System.out.println("unmodifiableList.add -> " + e.getClass().getSimpleName());
        }
    }
}
