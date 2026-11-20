// Lesson 5, part 3 — generics, type erasure, bounded types, wildcards (PECS), enums.
// Compile: javac TypeDemo3.java    Run: java TypeDemo3
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeDemo3 {

    // ---------- 1. A generic class: one blueprint, any element type ----------
    static class Box<T> {
        private T item;
        void put(T item) { this.item = item; }
        T get()          { return item; }
    }

    // ---------- 2. A generic method: <T> is declared before the return type ----------
    static <T> T firstOrDefault(List<T> list, T fallback) {
        return list.isEmpty() ? fallback : list.get(0);
    }

    // ---------- 3. A bounded type: "any T, as long as it can be compared" ----------
    static <T extends Comparable<T>> T max(List<T> list) {
        T best = list.get(0);
        for (T candidate : list)
            if (candidate.compareTo(best) > 0) best = candidate;
        return best;
    }

    // ---------- 4. PECS: read from a producer, write into a consumer ----------
    static double sum(List<? extends Number> producer) {      // we only READ
        double total = 0;
        for (Number n : producer) total += n.doubleValue();
        return total;
    }

    static void addIntegers(List<? super Integer> consumer) { // we only WRITE
        consumer.add(1);
        consumer.add(2);
    }

    // ---------- 5. An enum: a type with a fixed, complete set of values ----------
    enum Status {
        OPEN("awaiting action"),
        CLOSED("finished");

        private final String blurb;
        Status(String blurb) { this.blurb = blurb; }
        String blurb() { return blurb; }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. generics remove casts and catch mistakes early ---");
        Box<String> box = new Box<String>();
        box.put("hello");
        String s = box.get();                 // no cast needed
        // box.put(42);                       // would not compile — that is the point
        System.out.println("   box.get() = " + s + " (length " + s.length() + ")");

        System.out.println();
        System.out.println("--- 2. erasure: the type argument is gone at run time ---");
        List<String> strings = new ArrayList<String>();
        List<Integer> ints = new ArrayList<Integer>();
        System.out.println("   strings.getClass() = " + strings.getClass().getName());
        System.out.println("   ints.getClass()    = " + ints.getClass().getName());
        System.out.println("   same runtime class? " + (strings.getClass() == ints.getClass()));

        System.out.println();
        System.out.println("--- 3. so a raw reference can smuggle in the wrong type ---");
        List raw = strings;                   // raw type: all generic checks switched off
        raw.add(Integer.valueOf(42));         // compiles with a warning, succeeds at run time
        try {
            String oops = strings.get(0);     // the compiler inserted a hidden cast here
            System.out.println(oops);
        } catch (ClassCastException e) {
            System.out.println("   ClassCastException on a line with no visible cast: " + e.getMessage());
        }
        strings.clear();

        System.out.println();
        System.out.println("--- 4. generic methods and bounds ---");
        System.out.println("   firstOrDefault(empty, \"none\") = " + firstOrDefault(new ArrayList<String>(), "none"));
        System.out.println("   max([3, 9, 4])                = " + max(Arrays.asList(3, 9, 4)));
        System.out.println("   max([\"pear\", \"apple\"])        = " + max(Arrays.asList("pear", "apple")));

        System.out.println();
        System.out.println("--- 5. PECS in action ---");
        System.out.println("   sum(List<Integer>) = " + sum(Arrays.asList(1, 2, 3)));
        System.out.println("   sum(List<Double>)  = " + sum(Arrays.asList(1.5, 2.5)));
        List<Number> sink = new ArrayList<Number>();
        addIntegers(sink);                    // List<Number> is a valid List<? super Integer>
        System.out.println("   after addIntegers(List<Number>) = " + sink);

        System.out.println();
        System.out.println("--- 6. enums are types with a known, complete set of values ---");
        for (Status st : Status.values())
            System.out.println("   " + st + " (ordinal " + st.ordinal() + ") = " + st.blurb());
        System.out.println("   Status.valueOf(\"OPEN\") == Status.OPEN ? " + (Status.valueOf("OPEN") == Status.OPEN));
    }
}
