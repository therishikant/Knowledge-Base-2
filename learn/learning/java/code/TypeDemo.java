// Lesson 5, part 1 — classes, objects, encapsulation, constructors, static vs final.
// Compile: javac TypeDemo.java     Run: java TypeDemo
public class TypeDemo {

    // ---------- A class is a blueprint. This one models one bank account. ----------
    static class Account {

        // static = belongs to the CLASS, shared by every object ever made.
        private static int accountsOpened = 0;

        // final = assigned exactly once, then frozen for the life of the object.
        private final String id;

        // plain instance field = every object gets its own copy.
        private long balancePaise;

        // Main constructor: the ONLY door into a valid Account.
        Account(String owner, long openingPaise) {
            if (owner == null || owner.trim().isEmpty())
                throw new IllegalArgumentException("owner required");
            if (openingPaise < 0)
                throw new IllegalArgumentException("opening balance cannot be negative");
            accountsOpened++;
            this.id = owner.trim().toUpperCase() + "-" + accountsOpened;
            this.balancePaise = openingPaise;
        }

        // Overloaded convenience constructor: delegates with this(...) instead of duplicating rules.
        Account(String owner) {
            this(owner, 0L);
        }

        // Behaviour is public; the field it guards stays private.
        void deposit(long paise) {
            if (paise <= 0) throw new IllegalArgumentException("deposit must be positive");
            balancePaise += paise;
        }

        boolean withdraw(long paise) {
            if (paise <= 0) throw new IllegalArgumentException("withdrawal must be positive");
            if (paise > balancePaise) return false;   // the invariant: never go below zero
            balancePaise -= paise;
            return true;
        }

        // A getter returns a value, never the mutable internals.
        long balancePaise() { return balancePaise; }
        String id()         { return id; }

        static int accountsOpened() { return accountsOpened; }

        @Override public String toString() {
            return id + " = " + (balancePaise / 100.0);
        }
    }

    // ---------- A class with a PUBLIC field, to show what encapsulation prevents. ----------
    static class LeakyAccount {
        public long balancePaise;     // anyone can write anything
    }

    public static void main(String[] args) {
        System.out.println("--- 1. blueprint vs object ---");
        Account a = new Account("asha", 5000L);
        Account b = new Account("bala");
        System.out.println(a);
        System.out.println(b);
        System.out.println("a and b are different objects? " + (a != b));

        System.out.println();
        System.out.println("--- 2. static is shared, instance fields are not ---");
        System.out.println("accountsOpened (class-level) = " + Account.accountsOpened());
        a.deposit(2500L);
        System.out.println("after a.deposit: a = " + a.balancePaise() + ", b = " + b.balancePaise());

        System.out.println();
        System.out.println("--- 3. the constructor is the gate: invalid objects cannot exist ---");
        try {
            new Account("  ", 100L);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- 4. encapsulation protects the invariant ---");
        System.out.println("a.withdraw(100000 paise) allowed? " + a.withdraw(100000L));
        System.out.println("a balance still = " + a.balancePaise());

        LeakyAccount leaky = new LeakyAccount();
        leaky.balancePaise = -999;    // compiles happily — nothing guards it
        System.out.println("leaky balance = " + leaky.balancePaise + "  (impossible state)");

        System.out.println();
        System.out.println("--- 5. final means 'cannot be re-pointed' ---");
        // a.id = "X";   // would not compile: cannot assign a value to final variable id
        final StringBuilder note = new StringBuilder("hello");
        note.append(" world");        // allowed: the OBJECT is not frozen, only the variable
        System.out.println("final variable, mutated object: " + note);

        System.out.println();
        System.out.println("--- 6. == asks 'same object?', equals asks 'same contents?' ---");
        String s1 = new String("abc");
        String s2 = new String("abc");
        System.out.println("s1 == s2      ? " + (s1 == s2));
        System.out.println("s1.equals(s2) ? " + s1.equals(s2));
    }
}
