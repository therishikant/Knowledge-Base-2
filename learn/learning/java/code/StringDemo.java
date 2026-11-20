// String immutability, the string pool, == vs equals, and StringBuilder.
public class StringDemo {
    public static void main(String[] args) {
        String a = "hello";                 // goes in the pool
        String b = "hello";                 // same pooled object
        String c = new String("hello");     // a brand-new object on the heap

        System.out.println("a == b:          " + (a == b));           // true  — same pooled ref
        System.out.println("a == c:          " + (a == c));           // false — different object
        System.out.println("a.equals(c):     " + a.equals(c));        // true  — same characters
        System.out.println("a == c.intern(): " + (a == c.intern()));  // true  — intern returns pooled

        // Strings are immutable: concat makes a NEW string, the old one is unchanged.
        String s = "a";
        String s2 = s.concat("b");
        System.out.println("s = " + s + ", s2 = " + s2);

        // StringBuilder is the mutable way to build text in a loop (no garbage per step).
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) sb.append(i);
        System.out.println("built: " + sb);

        // Joins the compiler can do itself land in the pool; joins done at run time do not.
        String var = "ja";
        final String constant = "ja";
        System.out.println("\"java\" == \"ja\"+\"va\"       : " + ("java" == "ja" + "va"));
        System.out.println("\"java\" == var+\"va\"        : " + ("java" == var + "va"));
        System.out.println("\"java\" == constant+\"va\"   : " + ("java" == constant + "va"));
    }
}
