import java.util.Arrays;

// Part 1 of the memory cluster: what a variable holds, and what copying really copies.
public class MemoryDemo2 {

    static class Engine {
        int horsepower;
        Engine(int horsepower) { this.horsepower = horsepower; }
    }

    static class Car {
        String name;
        Engine engine;

        Car(String name, Engine engine) {
            this.name = name;
            this.engine = engine;
        }

        // Shallow copy: new Car box, but the SAME engine object is shared.
        Car shallowCopy() {
            return new Car(this.name, this.engine);
        }

        // Deep copy: new Car box AND a new Engine of its own.
        Car deepCopy() {
            return new Car(this.name, new Engine(this.engine.horsepower));
        }
    }

    static void changeNumber(int n) {
        n = 999;
    }

    static void changeInside(Engine e) {
        e.horsepower = 999;
    }

    static void pointSomewhereElse(Engine e) {
        e = new Engine(999);
    }

    public static void main(String[] args) {
        System.out.println("--- 1. primitives copy the value ---");
        int a = 10;
        int b = a;
        b = 20;
        System.out.println("a = " + a + ", b = " + b);

        System.out.println();
        System.out.println("--- 2. references copy the arrow, not the object ---");
        Engine e1 = new Engine(100);
        Engine e2 = e1;
        e2.horsepower = 250;
        System.out.println("e1.horsepower = " + e1.horsepower);
        System.out.println("e1 == e2 ? " + (e1 == e2));

        System.out.println();
        System.out.println("--- 3. Java always passes by value ---");
        int n = 10;
        changeNumber(n);
        System.out.println("after changeNumber(n), n = " + n);

        Engine e3 = new Engine(100);
        changeInside(e3);
        System.out.println("after changeInside(e3), hp = " + e3.horsepower);

        Engine e4 = new Engine(100);
        pointSomewhereElse(e4);
        System.out.println("after pointSomewhereElse(e4), hp = " + e4.horsepower);

        System.out.println();
        System.out.println("--- 4. shallow vs deep copy ---");
        Car original = new Car("Civic", new Engine(150));
        Car shallow = original.shallowCopy();
        Car deep = original.deepCopy();

        shallow.engine.horsepower = 500;
        System.out.println("after editing shallow copy, original hp = " + original.engine.horsepower);

        deep.engine.horsepower = 1;
        System.out.println("after editing deep copy,    original hp = " + original.engine.horsepower);

        System.out.println();
        System.out.println("--- 5. arrays hold arrows too ---");
        Engine[] garage = { new Engine(100), new Engine(200) };
        Engine[] copy = Arrays.copyOf(garage, garage.length); // shallow: same engines inside
        copy[0].horsepower = 777;
        System.out.println("garage[0].horsepower = " + garage[0].horsepower);
        System.out.println("garage[0] == copy[0] ? " + (garage[0] == copy[0]));
        System.out.println("garage    == copy    ? " + (garage == copy));
    }
}
