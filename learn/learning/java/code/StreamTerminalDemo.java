import java.util.*;
import java.util.stream.*;

// Part 5: every terminal operation, and reduce from first principles.
public class StreamTerminalDemo {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("ann", "bob", "carol", "dave", "erin");
        List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);

        System.out.println("-- produce a value --");
        System.out.println("count    : " + names.stream().count());
        System.out.println("min      : " + nums.stream().min(Comparator.naturalOrder()).get());
        System.out.println("max      : " + nums.stream().max(Comparator.naturalOrder()).get());
        System.out.println("sum(int) : " + nums.stream().mapToInt(Integer::intValue).sum());
        System.out.println("average  : " + nums.stream().mapToInt(Integer::intValue).average().getAsDouble());
        IntSummaryStatistics st = nums.stream().mapToInt(Integer::intValue).summaryStatistics();
        System.out.println("stats    : " + st);

        System.out.println("-- reduce, the three forms --");
        Optional<Integer> r1 = nums.stream().reduce((a, b) -> a + b);              // no seed -> Optional
        int r2 = nums.stream().reduce(0, (a, b) -> a + b);                          // seed -> plain value
        int r3 = names.stream().reduce(0, (acc, n) -> acc + n.length(), Integer::sum); // seed + combiner
        System.out.println("reduce1  : " + r1.get());
        System.out.println("reduce2  : " + r2);
        System.out.println("reduce3  : " + r3);
        System.out.println("empty+seed  : " + new ArrayList<Integer>().stream().reduce(0, Integer::sum));
        System.out.println("empty-seed  : " + new ArrayList<Integer>().stream().reduce(Integer::sum));
        System.out.println("reduce max  : " + nums.stream().reduce(Integer::max).get());
        System.out.println("reduce join : " + names.stream().reduce("", (a, b) -> a.isEmpty() ? b : a + "|" + b));

        System.out.println("-- ask a question (short-circuit) --");
        System.out.println("anyMatch : " + names.stream().anyMatch(n -> n.startsWith("c")));
        System.out.println("allMatch : " + names.stream().allMatch(n -> n.length() >= 3));
        System.out.println("noneMatch: " + names.stream().noneMatch(n -> n.isEmpty()));
        System.out.println("findFirst: " + names.stream().filter(n -> n.length() == 4).findFirst().get());
        System.out.println("findAny  : " + names.stream().filter(n -> n.length() == 4).findAny().get());

        System.out.println("-- the empty-stream trap --");
        List<String> none = new ArrayList<>();
        System.out.println("allMatch on empty  : " + none.stream().allMatch(n -> false));
        System.out.println("anyMatch on empty  : " + none.stream().anyMatch(n -> true));
        System.out.println("noneMatch on empty : " + none.stream().noneMatch(n -> true));

        System.out.println("-- consume / collect --");
        names.stream().limit(2).forEach(n -> System.out.println("forEach  : " + n));
        System.out.println("toArray  : " + Arrays.toString(names.stream().toArray(String[]::new)));
        System.out.println("toList   : " + names.stream().collect(Collectors.toList()));

        System.out.println("-- forEach vs forEachOrdered on a parallel stream --");
        StringBuilder a = new StringBuilder(), b = new StringBuilder();
        IntStream.range(0, 8).parallel().forEach(i -> a.append(i));
        IntStream.range(0, 8).parallel().forEachOrdered(i -> b.append(i));
        System.out.println("forEach        : " + a + "   <- order not guaranteed");
        System.out.println("forEachOrdered : " + b + "   <- always encounter order");
    }
}
