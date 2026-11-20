import java.util.*;
import java.util.stream.*;

// A Stream pipeline: lazy intermediate steps + one terminal step that triggers the work.
public class StreamDemo {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("ann", "bob", "carol", "dave", "erin");

        String r = names.stream()
                .filter(n -> n.length() > 3)          // intermediate — lazy
                .map(String::toUpperCase)             // intermediate — lazy
                .sorted()                             // intermediate — lazy
                .collect(Collectors.joining(", "));   // terminal — runs the pipeline
        System.out.println(r);

        int sum = IntStream.rangeClosed(1, 5).map(n -> n * n).sum();
        System.out.println("sum of squares 1..5 = " + sum);

        // Optional models "maybe a value" without null.
        Optional<String> first = names.stream().filter(n -> n.startsWith("z")).findFirst();
        System.out.println("found: " + first.orElse("(none)"));

        long evens = Stream.of(1, 2, 3, 4, 5).filter(n -> n % 2 == 0).count();
        System.out.println("evens = " + evens);
    }
}
