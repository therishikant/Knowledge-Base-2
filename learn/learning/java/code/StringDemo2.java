// Part 2: building text — concatenation vs StringBuilder, capacity, everyday methods.
import java.util.Arrays;

public class StringDemo2 {
    public static void main(String[] args) {
        // 1. "+" inside a loop rebuilds the whole string every step; StringBuilder does not.
        long t1 = System.nanoTime();
        String s = "";
        for (int i = 0; i < 20000; i++) s += "x";
        long t2 = System.nanoTime();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20000; i++) sb.append("x");
        String s2 = sb.toString();
        long t3 = System.nanoTime();

        System.out.println("concat  : " + s.length() + " chars in " + (t2 - t1) / 1000000 + " ms");
        System.out.println("builder : " + s2.length() + " chars in " + (t3 - t2) / 1000000 + " ms");

        // 2. A builder is mutable: append changes the SAME object and hands it back.
        StringBuilder b = new StringBuilder("ab");
        System.out.println("same object after append: " + (b == b.append("c")));
        System.out.println("b       : " + b);
        System.out.println("reversed: " + b.reverse());

        // 3. The builder owns an array that grows as (old * 2) + 2.
        StringBuilder g = new StringBuilder();
        System.out.println("capacity at start   : " + g.capacity());
        g.append("1234567890123456");
        System.out.println("capacity at 16 chars: " + g.capacity());
        g.append("7");
        System.out.println("capacity at 17 chars: " + g.capacity());

        // 4. Everyday read-only methods — each one returns a NEW string.
        String csv = "  ana,bob , cara  ";
        System.out.println("trim      : [" + csv.trim() + "]");
        String[] parts = csv.trim().split("\\s*,\\s*");
        System.out.println("split     : " + Arrays.toString(parts));
        System.out.println("join      : " + String.join(" | ", parts));
        System.out.println("replace   : " + csv.trim().replace(',', ';'));
        System.out.println("substring : " + "hello world".substring(6));
        System.out.println("indexOf   : " + "hello world".indexOf('o'));
        System.out.println("contains  : " + "hello".contains("ell"));
        System.out.println("compareTo : " + "apple".compareTo("banana"));
        System.out.println("format    : " + String.format("%s scored %.1f%%", "ana", 91.25));

        // 5. The original is never touched by any of it.
        System.out.println("csv still : [" + csv + "]");
    }
}
