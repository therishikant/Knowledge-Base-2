import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Part 8: the rest of Java 8 - default/static interface methods and java.time.
public class Java8MiscDemo {

    interface Vehicle {
        String name();                                   // abstract
        default String describe() {                      // body on an interface
            return "a " + name() + " with " + wheels() + " wheels";
        }
        default int wheels() { return 4; }
        static Vehicle of(String n) { return () -> n; }   // static factory on the interface
    }

    static class Bike implements Vehicle {
        public String name() { return "bike"; }
        public int wheels() { return 2; }                 // override the default
    }

    interface A { default String hello() { return "A"; } }
    interface B { default String hello() { return "B"; } }
    static class C implements A, B {
        public String hello() { return A.super.hello() + B.super.hello(); } // diamond must be resolved
    }

    public static void main(String[] args) {
        System.out.println("-- default & static interface methods --");
        System.out.println("inherited : " + Vehicle.of("car").describe());
        System.out.println("overridden: " + new Bike().describe());
        System.out.println("diamond   : " + new C().hello());

        System.out.println("-- java.time --");
        LocalDate d = LocalDate.of(2024, Month.MARCH, 15);
        System.out.println("LocalDate : " + d);
        System.out.println("plusDays  : " + d.plusDays(20) + "   (original still " + d + " - immutable)");
        System.out.println("LocalTime : " + LocalTime.of(9, 30));
        System.out.println("LocalDT   : " + LocalDateTime.of(d, LocalTime.of(9, 30)));
        System.out.println("ZonedDT   : " + ZonedDateTime.of(d, LocalTime.of(9, 30), ZoneId.of("Asia/Kolkata")));
        System.out.println("Instant   : " + Instant.ofEpochSecond(0));
        System.out.println("Duration  : " + Duration.ofMinutes(90));
        System.out.println("Period    : " + Period.between(d, LocalDate.of(2024, 12, 25)));
        System.out.println("daysUntil : " + ChronoUnit.DAYS.between(d, LocalDate.of(2024, 12, 25)));
        System.out.println("format    : " + d.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)));
        System.out.println("parse     : " + LocalDate.parse("2024-03-15"));
        System.out.println("compare   : " + d.isBefore(LocalDate.of(2025, 1, 1)));

        System.out.println("-- why the old API was replaced --");
        Date old = new Date(0);
        System.out.println("Date year : " + (old.getYear()) + "  <- 70, because years count from 1900");
        Calendar c = Calendar.getInstance();
        c.set(2024, 0, 15);                               // month 0 = January
        System.out.println("Calendar  : month 0 means January - a classic off-by-one bug source");
    }
}
