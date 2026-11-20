import java.util.*;

// Part 2 demo: how a HashMap turns a key into a bucket, and how the map flavours differ. Java 8 compatible.
public class CollectionsDemo3 {

    // The same two steps HashMap does internally: spread the hash, then mask it to the table size.
    static int bucketOf(Object key, int tableSize) {
        int h = key.hashCode();
        h = h ^ (h >>> 16);
        return h & (tableSize - 1);
    }

    public static void main(String[] args) {
        System.out.println("--- 1. key -> hashCode -> bucket (table of 16) ---");
        for (String k : new String[]{"apple", "banana", "cherry", "Aa", "BB"}) {
            System.out.println(pad(k) + " hashCode=" + pad2(k.hashCode()) + "  bucket=" + bucketOf(k, 16));
        }
        System.out.println("\"Aa\".equals(\"BB\")? " + "Aa".equals("BB")
                + "  -> same bucket, different keys = a collision");

        System.out.println();
        System.out.println("--- 2. A bigger table moves keys to new buckets ---");
        System.out.println("\"apple\" in a 16-slot table : " + bucketOf("apple", 16));
        System.out.println("\"apple\" in a 32-slot table : " + bucketOf("apple", 32));

        System.out.println();
        System.out.println("--- 3. Three maps, three iteration orders ---");
        Map<String, Integer> hash   = new HashMap<String, Integer>();
        Map<String, Integer> linked = new LinkedHashMap<String, Integer>();
        Map<String, Integer> tree   = new TreeMap<String, Integer>();
        for (String k : new String[]{"banana", "apple", "cherry"}) {
            hash.put(k, 1); linked.put(k, 1); tree.put(k, 1);
        }
        System.out.println("HashMap       = " + hash.keySet());   // no promise at all
        System.out.println("LinkedHashMap = " + linked.keySet()); // insertion order
        System.out.println("TreeMap       = " + tree.keySet());   // sorted order

        System.out.println();
        System.out.println("--- 4. LinkedHashMap in access-order = a tiny LRU cache ---");
        LinkedHashMap<String, Integer> lru = new LinkedHashMap<String, Integer>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > 3; // keep only the 3 most recently used
            }
        };
        lru.put("a", 1); lru.put("b", 2); lru.put("c", 3);
        lru.get("a");       // touching "a" makes it the newest
        lru.put("d", 4);    // cache is full -> the oldest ("b") is dropped
        System.out.println("after get(a) then put(d): " + lru.keySet());

        System.out.println();
        System.out.println("--- 5. TreeMap can answer range questions ---");
        TreeMap<Integer, String> prices = new TreeMap<Integer, String>();
        prices.put(100, "pen"); prices.put(250, "mug"); prices.put(900, "chair");
        System.out.println("firstKey()      = " + prices.firstKey());
        System.out.println("floorKey(300)   = " + prices.floorKey(300));   // biggest key <= 300
        System.out.println("ceilingKey(300) = " + prices.ceilingKey(300)); // smallest key >= 300
        System.out.println("headMap(900)    = " + prices.headMap(900));    // everything cheaper than 900

        System.out.println();
        System.out.println("--- 6. Java 8 map helpers: counting words ---");
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String w : "tea coffee tea milk tea".split(" ")) {
            counts.merge(w, 1, new java.util.function.BiFunction<Integer, Integer, Integer>() {
                public Integer apply(Integer oldV, Integer newV) { return oldV + newV; }
            });
        }
        System.out.println("counts            = " + new TreeMap<String, Integer>(counts));
        System.out.println("getOrDefault(juice) = " + counts.getOrDefault("juice", 0));

        System.out.println();
        System.out.println("--- 7. null keys ---");
        Map<String, String> hm = new HashMap<String, String>();
        hm.put(null, "allowed in HashMap");
        System.out.println("HashMap.get(null)   = " + hm.get(null));
        try {
            new TreeMap<String, String>().put(null, "x");
        } catch (NullPointerException e) {
            System.out.println("TreeMap.put(null)   -> NullPointerException (it must compare keys)");
        }
        try {
            new Hashtable<String, String>().put(null, "x");
        } catch (NullPointerException e) {
            System.out.println("Hashtable.put(null) -> NullPointerException (legacy rule)");
        }
    }

    static String pad(String s)  { return String.format("%-8s", "\"" + s + "\""); }
    static String pad2(int i)    { return String.format("%12d", i); }
}
