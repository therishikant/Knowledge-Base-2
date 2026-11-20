import java.util.*;
import java.util.function.*;

// Part 7: Optional - modelling "maybe absent" in the type instead of returning null.
public class OptionalDemo {

    static class Address { String city; Address(String c) { city = c; } Optional<String> getCity() { return Optional.ofNullable(city); } }
    static class User { Address address; User(Address a) { address = a; } Optional<Address> getAddress() { return Optional.ofNullable(address); } }

    static final Map<String, Integer> AGES = new HashMap<>();
    static { AGES.put("ann", 30); }

    static Optional<Integer> ageOf(String name) {
        return Optional.ofNullable(AGES.get(name));
    }

    public static void main(String[] args) {
        System.out.println("-- creating --");
        System.out.println("of        : " + Optional.of("x"));
        System.out.println("ofNullable: " + Optional.ofNullable(null));
        System.out.println("empty     : " + Optional.empty());
        try { Optional.of(null); } catch (NullPointerException e) { System.out.println("of(null)  : throws NullPointerException"); }

        System.out.println("-- reading safely --");
        System.out.println("orElse        : " + ageOf("zoe").orElse(-1));
        System.out.println("orElseGet     : " + ageOf("zoe").orElseGet(() -> expensiveDefault()));
        System.out.println("orElse eager  : " + ageOf("ann").orElse(expensiveDefault()) + "  <- default built even though value existed");
        System.out.println("orElseGet lazy: " + ageOf("ann").orElseGet(() -> expensiveDefault()) + "  <- supplier never ran");
        try { ageOf("zoe").orElseThrow(() -> new NoSuchElementException("no such user")); }
        catch (RuntimeException e) { System.out.println("orElseThrow   : " + e.getMessage()); }

        System.out.println("-- transforming --");
        System.out.println("map       : " + ageOf("ann").map(a -> a + 1).orElse(-1));
        System.out.println("filter    : " + ageOf("ann").filter(a -> a > 40).orElse(-1));
        System.out.println("isPresent : " + ageOf("ann").isPresent());
        ageOf("ann").ifPresent(a -> System.out.println("ifPresent : " + a));

        System.out.println("-- map vs flatMap --");
        User withCity = new User(new Address("pune"));
        User noAddress = new User(null);
        // getAddress() already returns an Optional, so map would give Optional<Optional<...>>
        Optional<Optional<Address>> doubled = Optional.of(withCity).map(User::getAddress);
        System.out.println("map       : " + doubled);
        System.out.println("flatMap   : " + Optional.of(withCity).flatMap(User::getAddress).flatMap(Address::getCity).orElse("unknown"));
        System.out.println("flatMap   : " + Optional.of(noAddress).flatMap(User::getAddress).flatMap(Address::getCity).orElse("unknown"));

        System.out.println("-- the null-check chain it replaces --");
        System.out.println("old style : " + oldStyle(noAddress));
    }

    static int expensiveDefault() {
        System.out.println("   (building the default...)");
        return -99;
    }

    static String oldStyle(User u) {
        if (u != null) {
            Address a = u.address;
            if (a != null && a.city != null) return a.city;
        }
        return "unknown";
    }
}
