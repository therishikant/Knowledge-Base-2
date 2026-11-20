import java.math.BigDecimal;
import java.util.*;

// Part 3 demo: the equals/hashCode contract and ordering. Java 8 compatible.
public class CollectionsDemo {

    // Correct: equals and hashCode are built from the SAME fields.
    static final class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
        @Override public String toString() { return "(" + x + "," + y + ")"; }
    }

    // Broken: equals overridden, hashCode left as the identity default.
    static final class BrokenPoint {
        final int x, y;
        BrokenPoint(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof BrokenPoint)) return false;
            BrokenPoint p = (BrokenPoint) o;
            return x == p.x && y == p.y;
        }
    }

    // Mutable: changing x after insertion strands the entry in the old bucket.
    static final class MutablePoint {
        int x, y;
        MutablePoint(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof MutablePoint)) return false;
            MutablePoint p = (MutablePoint) o;
            return x == p.x && y == p.y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
    }

    static final class User {
        final String name; final int age;
        User(String name, int age) { this.name = name; this.age = age; }
        public String getName() { return name; }
        public int getAge() { return age; }
        @Override public String toString() { return name + "/" + age; }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Both methods overridden: a fresh object is found ---");
        Set<Point> good = new HashSet<Point>();
        good.add(new Point(1, 2));
        System.out.println("contains a fresh (1,2)? " + good.contains(new Point(1, 2)));
        System.out.println("equal objects share a hash? "
                + (new Point(1, 2).hashCode() == new Point(1, 2).hashCode()));

        System.out.println();
        System.out.println("--- 2. equals without hashCode: the object vanishes ---");
        Set<BrokenPoint> broken = new HashSet<BrokenPoint>();
        broken.add(new BrokenPoint(1, 2));
        System.out.println("equals says equal?      " + new BrokenPoint(1, 2).equals(new BrokenPoint(1, 2)));
        System.out.println("contains a fresh (1,2)? " + broken.contains(new BrokenPoint(1, 2)));

        System.out.println();
        System.out.println("--- 3. Mutating a key after inserting it ---");
        MutablePoint key = new MutablePoint(1, 2);
        Set<MutablePoint> mutable = new HashSet<MutablePoint>();
        mutable.add(key);
        key.x = 99; // the hash changed, but the entry did not move
        System.out.println("contains(the very same object)? " + mutable.contains(key));
        System.out.println("set is not empty though, size = " + mutable.size());

        System.out.println();
        System.out.println("--- 4. Comparable (one built-in order) vs Comparator (any order) ---");
        List<String> words = new ArrayList<String>(Arrays.asList("pear", "Fig", "apple"));
        Collections.sort(words); // natural order: String implements Comparable
        System.out.println("natural order    = " + words);
        Collections.sort(words, String.CASE_INSENSITIVE_ORDER);
        System.out.println("case-insensitive = " + words);

        List<User> users = new ArrayList<User>(Arrays.asList(
                new User("ana", 30), new User("bob", 25), new User("ana", 25)));
        Collections.sort(users, Comparator.comparing(new java.util.function.Function<User, String>() {
            public String apply(User u) { return u.getName(); }
        }).thenComparing(new java.util.function.Function<User, Integer>() {
            public Integer apply(User u) { return Integer.valueOf(u.getAge()); }
        }));
        System.out.println("name, then age   = " + users);

        System.out.println();
        System.out.println("--- 5. When compareTo disagrees with equals ---");
        BigDecimal a = new BigDecimal("1.0"), b = new BigDecimal("1.00");
        System.out.println("a.equals(b)?       " + a.equals(b));
        System.out.println("a.compareTo(b)==0? " + (a.compareTo(b) == 0));
        Set<BigDecimal> hashSet = new HashSet<BigDecimal>(Arrays.asList(a, b));
        Set<BigDecimal> treeSet = new TreeSet<BigDecimal>(Arrays.asList(a, b));
        System.out.println("HashSet size = " + hashSet.size() + "  (uses equals)");
        System.out.println("TreeSet size = " + treeSet.size() + "  (uses compareTo)");
    }
}
