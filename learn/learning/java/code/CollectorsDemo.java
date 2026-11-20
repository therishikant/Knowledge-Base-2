import java.util.*;
import java.util.stream.*;

// Part 6: the Collectors catalogue, with groupingBy taken all the way down.
public class CollectorsDemo {

    static class Employee {
        final String name, dept, city;
        final int salary;
        Employee(String name, String dept, String city, int salary) {
            this.name = name; this.dept = dept; this.city = city; this.salary = salary;
        }
        String getName() { return name; }
        String getDept() { return dept; }
        String getCity() { return city; }
        int getSalary() { return salary; }
        public String toString() { return name; }
    }

    static final List<Employee> STAFF = Arrays.asList(
            new Employee("ann",   "eng",   "pune",  120),
            new Employee("bob",   "eng",   "pune",  100),
            new Employee("carol", "eng",   "delhi", 140),
            new Employee("dave",  "sales", "pune",   90),
            new Employee("erin",  "sales", "delhi", 110),
            new Employee("frank", "hr",    "delhi",  70));

    public static void main(String[] args) {
        System.out.println("-- level 1: into a container --");
        System.out.println("toList   : " + STAFF.stream().map(Employee::getName).collect(Collectors.toList()));
        System.out.println("toSet    : " + STAFF.stream().map(Employee::getDept).collect(Collectors.toSet()));
        System.out.println("toColl   : " + STAFF.stream().map(Employee::getDept).collect(Collectors.toCollection(TreeSet::new)));
        System.out.println("joining  : " + STAFF.stream().map(Employee::getName).collect(Collectors.joining(", ", "[", "]")));

        System.out.println("-- level 2: into a number --");
        System.out.println("counting : " + STAFF.stream().collect(Collectors.counting()));
        System.out.println("summing  : " + STAFF.stream().collect(Collectors.summingInt(Employee::getSalary)));
        System.out.println("averaging: " + STAFF.stream().collect(Collectors.averagingInt(Employee::getSalary)));
        System.out.println("maxBy    : " + STAFF.stream().collect(Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))).get());
        System.out.println("summarize: " + STAFF.stream().collect(Collectors.summarizingInt(Employee::getSalary)));

        System.out.println("-- level 3: toMap --");
        System.out.println("toMap    : " + STAFF.stream().collect(Collectors.toMap(Employee::getName, Employee::getSalary)));
        try {
            STAFF.stream().collect(Collectors.toMap(Employee::getDept, Employee::getName));
        } catch (IllegalStateException e) {
            System.out.println("dup key  : " + e.getMessage());
        }
        System.out.println("toMap+mrg: " + STAFF.stream().collect(
                Collectors.toMap(Employee::getDept, Employee::getName, (x, y) -> x + "+" + y)));
        System.out.println("toMap+sup: " + STAFF.stream().collect(
                Collectors.toMap(Employee::getDept, Employee::getName, (x, y) -> x + "+" + y, TreeMap::new)));

        System.out.println("-- level 4: groupingBy --");
        System.out.println("1-arg    : " + STAFF.stream().collect(Collectors.groupingBy(Employee::getDept)));
        System.out.println("+counting: " + STAFF.stream().collect(
                Collectors.groupingBy(Employee::getDept, Collectors.counting())));
        System.out.println("+summing : " + STAFF.stream().collect(
                Collectors.groupingBy(Employee::getDept, Collectors.summingInt(Employee::getSalary))));
        System.out.println("+mapping : " + STAFF.stream().collect(
                Collectors.groupingBy(Employee::getDept, Collectors.mapping(Employee::getName, Collectors.toList()))));
        System.out.println("+TreeMap : " + STAFF.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new, Collectors.counting())));
        System.out.println("nested   : " + STAFF.stream().collect(
                Collectors.groupingBy(Employee::getDept, Collectors.groupingBy(Employee::getCity))));
        System.out.println("computed : " + STAFF.stream().collect(
                Collectors.groupingBy(e -> e.getSalary() >= 110 ? "senior" : "junior", Collectors.counting())));

        System.out.println("-- maxBy inside a group needs collectingAndThen --");
        System.out.println("raw      : " + STAFF.stream().collect(Collectors.groupingBy(Employee::getDept,
                Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)))));
        System.out.println("unwrapped: " + STAFF.stream().collect(Collectors.groupingBy(Employee::getDept,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)),
                        Optional::get))));

        System.out.println("-- level 5: partitioningBy (always 2 keys) --");
        System.out.println("partition: " + STAFF.stream().collect(Collectors.partitioningBy(e -> e.getSalary() > 100)));
        System.out.println("+counting: " + STAFF.stream().collect(
                Collectors.partitioningBy(e -> e.getSalary() > 100, Collectors.counting())));
        System.out.println("empty hlf: " + STAFF.stream().collect(Collectors.partitioningBy(e -> e.getSalary() > 999)));

        System.out.println("-- classic interview recipes --");
        String text = "the quick brown fox jumps over the lazy dog the end";
        System.out.println("word count : " + Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting())));
        System.out.println("char freq  : " + "banana".chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting())));
        System.out.println("first dup  : " + findFirstDuplicate(Arrays.asList(1, 2, 3, 2, 1)));
        System.out.println("by length  : " + Stream.of("ann", "bob", "carol", "dave")
                .collect(Collectors.groupingBy(String::length, Collectors.toList())));
        System.out.println("sort map v : " + sortMapByValue(Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))));
        System.out.println("anagrams   : " + Stream.of("eat", "tea", "tan", "ate", "nat", "bat")
                .collect(Collectors.groupingBy(CollectorsDemo::sortLetters)));
        System.out.println("2nd highest: " + STAFF.stream().map(Employee::getSalary).distinct()
                .sorted(Comparator.reverseOrder()).skip(1).findFirst().get());
    }

    static String sortLetters(String s) {
        char[] c = s.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    static Integer findFirstDuplicate(List<Integer> xs) {
        Set<Integer> seen = new HashSet<>();
        return xs.stream().filter(x -> !seen.add(x)).findFirst().orElse(null);
    }

    static LinkedHashMap<String, Long> sortMapByValue(Map<String, Long> in) {
        return in.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }
}
