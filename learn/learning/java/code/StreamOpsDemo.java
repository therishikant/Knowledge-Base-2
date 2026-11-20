import java.util.*;
import java.util.stream.*;

// Part 3 & 4: how a pipeline really runs, and every intermediate operation.
public class StreamOpsDemo {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("ann", "bob", "carol", "dave", "erin");

        // --- PROOF 1: nothing runs without a terminal operation -------------
        System.out.println("-- building a pipeline with no terminal op --");
        names.stream().peek(n -> System.out.println("  peek: " + n)).filter(n -> n.length() > 3);
        System.out.println("  (nothing printed above - the pipeline never ran)");

        // --- PROOF 2: elements flow one at a time, not stage by stage -------
        System.out.println("-- element-at-a-time order --");
        names.stream()
             .filter(n -> { System.out.println("  filter " + n); return n.length() > 3; })
             .map(n -> { System.out.println("    map  " + n); return n.toUpperCase(); })
             .forEach(n -> System.out.println("      got " + n));

        // --- PROOF 3: short-circuiting stops early --------------------------
        System.out.println("-- findFirst short-circuits --");
        Optional<String> first = names.stream()
                .peek(n -> System.out.println("  saw " + n))
                .filter(n -> n.length() > 3)
                .findFirst();
        System.out.println("  first = " + first.get());

        // --- PROOF 4: a stream is one-shot ----------------------------------
        Stream<String> s = names.stream();
        s.count();
        try {
            s.count();
        } catch (IllegalStateException e) {
            System.out.println("-- reuse --\n  " + e.getMessage());
        }

        System.out.println("-- intermediate operations --");
        // filter: keep what matches
        System.out.println("filter   : " + names.stream().filter(n -> n.contains("a")).collect(Collectors.toList()));
        // map: transform one -> one
        System.out.println("map      : " + names.stream().map(String::length).collect(Collectors.toList()));
        // flatMap: flatten one -> many
        List<List<Integer>> nested = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));
        System.out.println("flatMap  : " + nested.stream().flatMap(List::stream).collect(Collectors.toList()));
        System.out.println("flatMap2 : " + Stream.of("a b", "c d").flatMap(x -> Arrays.stream(x.split(" "))).collect(Collectors.toList()));
        // distinct: uses equals/hashCode
        System.out.println("distinct : " + Stream.of(1, 2, 2, 3, 1).distinct().collect(Collectors.toList()));
        // sorted: natural and with a comparator
        System.out.println("sorted   : " + names.stream().sorted().collect(Collectors.toList()));
        System.out.println("sortedBy : " + names.stream().sorted(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder())).collect(Collectors.toList()));
        // limit / skip
        System.out.println("limit    : " + names.stream().limit(2).collect(Collectors.toList()));
        System.out.println("skip     : " + names.stream().skip(3).collect(Collectors.toList()));
        // mapToInt / boxed - the primitive bridge
        System.out.println("mapToInt : " + names.stream().mapToInt(String::length).sum());
        System.out.println("boxed    : " + IntStream.range(0, 4).boxed().collect(Collectors.toList()));

        System.out.println("-- sources --");
        System.out.println("of       : " + Stream.of("x", "y").collect(Collectors.toList()));
        System.out.println("range    : " + IntStream.range(1, 4).boxed().collect(Collectors.toList()));
        System.out.println("closed   : " + IntStream.rangeClosed(1, 4).boxed().collect(Collectors.toList()));
        System.out.println("iterate  : " + Stream.iterate(1, n -> n * 2).limit(5).collect(Collectors.toList()));
        System.out.println("generate : " + Stream.generate(() -> "ping").limit(3).collect(Collectors.toList()));
        System.out.println("chars    : " + "hello".chars().mapToObj(c -> (char) c).collect(Collectors.toList()));
        System.out.println("empty    : " + Stream.empty().collect(Collectors.toList()));

        System.out.println("-- ordering trap: the same two ops, swapped --");
        System.out.println("filter-then-skip : " + names.stream().filter(n -> n.length() > 3).skip(1).collect(Collectors.toList()));
        System.out.println("skip-then-filter : " + names.stream().skip(1).filter(n -> n.length() > 3).collect(Collectors.toList()));
    }
}
