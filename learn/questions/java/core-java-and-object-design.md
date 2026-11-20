# Core Java and Object Design - Interview Question Bank

[Back to index](index.md)

## Scope and evidence

This bank prepares you for senior Java and tech-lead interviews. It contains
**150 questions**, grouped by subtopic, with definitions, comparisons,
predict-the-output exercises, bug diagnosis, and design cases. Every question
has an explanation, a code example, and a source link. Read the answer, then
explain why the example behaves that way without looking.

These are **original practice questions grounded in public specifications and
APIs**, not a list of verified questions asked by particular employers. A source
verifies a technical rule, not how often interviewers ask it. No finite bank
guarantees an interview pass.

**Version baseline:** examples work on Java 8 unless marked otherwise. Version
notes cover Java 11, 17, 21, and 25 where behavior or syntax differs. Preview
features are not required. Records and sealed types appear only where they
affect core object design; deeper feature coverage belongs in
[Modern Java](modern-java-features.md). Collections internals, JVM internals,
concurrency, and performance remain in their separate banks.

**Example convention:** snippets are independent. Ordinary snippets go inside a
method that may declare `throws Exception`. `// Class members` means declarations
inside a surrounding class; `// Top-level declarations` means separate type
declarations. Import the named standard-library types; the examples use
`java.util`, `java.util.function`, `java.io`, `java.math`, `java.time`,
`java.time.format`, `java.nio.charset`, `java.lang.annotation`, and
`java.lang.reflect`. Lines labelled `Does not compile` are deliberately commented
out. Assertions can be checked with `java -ea`.

## Coverage map

| Section | Questions | Focus |
| --- | --- | --- |
| A | 1-15 | Types, conversions, operators, boxing |
| B | 16-30 | Methods, overloads, overriding, dispatch |
| C | 31-45 | Classes, interfaces, construction, access |
| D | 46-55 | Equality, hashing, ordering, Object contracts |
| E | 56-65 | Immutability, copying, ownership |
| F | 66-80 | Strings, Unicode, text processing |
| G | 81-95 | Exceptions and resource ownership |
| H | 96-105 | Lambdas and functional API design |
| I | 106-115 | Annotations, reflection, nested types, enums |
| J | 116-125 | I/O and serialization |
| K | 126-135 | Numbers, dates, time, null contracts |
| L | 136-145 | Senior object-design cases |
| M | 146-150 | Modern core design and compatibility |

**Difficulty:** Beginner = establish the rule; Intermediate = apply it and
explain edge cases; Advanced = reason about contracts, compatibility, or a
production failure. Question numbers are study identifiers, not a difficulty
ranking.

## A. Types, conversions, operators, and boxing

### 1. [Beginner] What are Java's primitive and reference types?

**Answer:** The eight primitives are `boolean`, `byte`, `short`, `char`, `int`,
`long`, `float`, and `double`. A primitive variable contains a primitive value.
A reference variable contains a reference to an object or `null`; arrays are
objects too. A reference is not a language-level raw memory address you can do
arithmetic on. Assignment copies the value, so copying a reference can make two
variables refer to the same mutable object.

```java
int a = 7;
int b = a;
b++;
int[] first = {7};
int[] second = first;
second[0]++;
assert a == 7;
assert first[0] == 8;
```

**Reference:** [JLS 4: Types and values][types].

### 2. [Beginner] What receives a default value, and what must be initialized explicitly?

**Answer:** Instance fields, static fields, and array elements receive defaults:
numeric zero, `false`, or `null`, depending on type. Local variables must be
definitely assigned before use. This distinction prevents reading an
uninitialized local, but it does not prevent accidentally using a default-valued
field. Constructors should establish meaningful domain values rather than
assuming that zero or `null` is always valid.

```java
class Settings {
    int retries;
    boolean enabled;
    String name;
}
Settings s = new Settings();
assert s.retries == 0 && !s.enabled && s.name == null;
int local;
// Does not compile: System.out.println(local);
```

**Reference:** [JLS 4.12.5: Initial values][types].

### 3. [Beginner] Is Java pass-by-value? Why can a method change an object?

**Answer:** Java always passes values. For an object argument, the copied value
is a reference. Both references initially identify the same object, so mutation
is visible through either reference. Reassigning the parameter changes only the
method's local copy. Draw two arrows to one object to reason about this; do not
describe it as "objects are passed by reference."

```java
class Editor {
    void change(StringBuilder text) {
        text.append("!");
        text = new StringBuilder("replacement");
    }
}
StringBuilder text = new StringBuilder("hello");
new Editor().change(text);
assert text.toString().equals("hello!");
```

**Reference:** [JLS 15.12.4.5: Create frame and assign argument values][expressions].

### 4. [Intermediate] Can a widening primitive conversion lose information?

**Answer:** Yes. Widening is about permitted type conversion, not guaranteed
exactness. `int` to `long` is exact, but `int` to `float`, `long` to `float`,
and `long` to `double` can lose precision. They do not throw just because
precision is lost. For identifiers, counters, and monetary quantities, choose
types according to exactness requirements, not only their apparent size.

```java
int exact = 16_777_217;
float rounded = exact;
assert (int) rounded == 16_777_216;
long preserved = exact;
assert preserved == 16_777_217L;
```

**Reference:** [JLS 5.1.2: Widening primitive conversion][conversions].

### 5. [Intermediate] What does narrowing do? Does it perform range validation?

**Answer:** A narrowing integer conversion discards high bits; it does not
reject an out-of-range value. For floating-point-to-integer conversions, Java
first truncates toward zero, with specified handling for NaN and out-of-range
values. A cast is therefore not a validation strategy. Validate before casting,
or use an exact conversion method such as `Math.toIntExact` when available.

```java
assert (byte) 130 == -126;
assert (int) 3.9 == 3;
assert (int) -3.9 == -3;
assert (int) Double.NaN == 0;
long count = 42L;
int checked = Math.toIntExact(count);
assert checked == 42;
```

**Reference:** [JLS 5.1.3: Narrowing primitive conversion][conversions].

### 6. [Intermediate] Why does arithmetic on bytes produce an int?

**Answer:** Numeric promotion usually converts `byte`, `short`, and `char`
operands to `int` before arithmetic. Assignment of a constant expression that
fits the destination is a separate rule: `byte b = 1 + 2` is allowed, but adding
two ordinary byte variables produces an `int`. Understand the expression's type
before deciding whether its numeric value happens to fit.

```java
byte a = 1;
byte b = 2;
int total = a + b;
byte constant = 1 + 2;
// Does not compile: byte result = a + b;
assert total == constant;
```

**Reference:** [JLS 5.2 and 5.6: Assignment and numeric promotion][conversions].

### 7. [Intermediate] Why does `b += 1` compile when `b = b + 1` may not?

**Answer:** A compound assignment includes an implicit conversion back to the
left-hand type and evaluates the left-hand operand only once. That convenience
can hide truncation. It is not proof that the operation is safe. For important
counters or quantities, use an appropriate wider type and explicit checks.
Do not introduce a cast merely to silence a compiler error without checking
the intended range.

```java
byte b = 127;
b += 1;
assert b == -128;
// Does not compile: b = b + 1;
```

**Reference:** [JLS 15.26.2: Compound assignment][expressions].

### 8. [Intermediate] How do integer overflow and division behave?

**Answer:** Ordinary integer addition and multiplication wrap on overflow.
Integer division truncates toward zero and division by zero throws
`ArithmeticException`. The special overflow `Integer.MIN_VALUE / -1` returns
`Integer.MIN_VALUE`; it does not throw. Use exact arithmetic methods when
overflow must be rejected. Casting after an overflowing calculation is too
late: widen an operand before the operation.

```java
assert Integer.MAX_VALUE + 1 == Integer.MIN_VALUE;
assert -7 / 3 == -2;
assert Integer.MIN_VALUE / -1 == Integer.MIN_VALUE;
long correct = 1_000_000L * 1_000_000;
assert correct == 1_000_000_000_000L;
assert Math.addExact(20, 22) == 42;
```

**Reference:** [JLS 15.17-15.18: Arithmetic operators][expressions].

### 9. [Intermediate] How do NaN, infinity, signed zero, and floating-point equality behave?

**Answer:** Binary floating point cannot exactly represent every decimal
fraction. NaN is unequal to every value, including itself, under `==`; use
`Double.isNaN`. Primitive positive and negative zero compare equal, but their
reciprocals have different signs. Wrapper equality has different rules:
`Double.equals` distinguishes signed zeros and treats NaN values as equal.
Since Java 17, floating-point expressions are always strict; `strictfp` no
longer changes their evaluation.

```java
double nan = Double.NaN;
assert nan != nan;
assert Double.isNaN(nan);
assert 0.0 == -0.0;
assert !Double.valueOf(0.0).equals(Double.valueOf(-0.0));
assert 1.0 / 0.0 == Double.POSITIVE_INFINITY;
assert 0.1 + 0.2 != 0.3;
```

**References:** [JLS 15][expressions]; [JEP 306: Always-strict floating point][strictfp].

### 10. [Beginner] What are boxing and unboxing, and where does null cause trouble?

**Answer:** Boxing converts a primitive to its wrapper; unboxing extracts a
primitive from a wrapper. Unboxing `null` throws `NullPointerException`.
Arithmetic, comparisons, and conditions can trigger unboxing invisibly.
Use wrappers when absence is meaningful or an API requires an object, but
decide explicitly what absence means before performing arithmetic.

```java
Integer count = 4;
int next = count + 1;
assert next == 5;
Integer missing = null;
int fallback = missing == null ? 0 : missing;
assert fallback == 0;
// Would throw NullPointerException: int broken = missing + 1;
```

**Reference:** [JLS 5.1.7-5.1.8: Boxing and unboxing][conversions].

### 11. [Intermediate] Why is comparing wrapper objects with `==` unreliable?

**Answer:** Reference-to-reference `==` checks identity, not numeric equality.
Small boxed integer constants share identity under the language's specified
boxing guarantees; some implementations cache additional values. Do not rely
on identity for larger numbers, and do not assert that they must be different
objects either. `Objects.equals` handles two nullable references; unboxing is
appropriate only after establishing non-nullness.

```java
Integer a = 127;
Integer b = 127;
assert a == b;
Integer c = 1000;
Integer d = 1000;
assert c.equals(d);
assert Objects.equals(c, d);
assert !Integer.valueOf(1).equals(Long.valueOf(1));
```

**Reference:** [JLS 5.1.7: Boxing identity guarantees][conversions].

### 12. [Intermediate] What is the difference between `&&` and `&`, and between `||` and `|`?

**Answer:** For booleans, `&&` and `||` short-circuit: the right operand is
evaluated only if needed. `&` and `|` evaluate both operands; for integral types
they also perform bitwise operations. Guarding a nullable reference with `&`
can still dereference null. Prefer short-circuit operators for logical
conditions unless evaluating both sides is an explicit requirement.

```java
String name = null;
assert !(name != null && name.length() > 0);
int permissions = 0b0101;
assert (permissions & 0b0001) != 0;
// Would throw NullPointerException:
// boolean broken = name != null & name.length() > 0;
```

**Reference:** [JLS 15.22-15.24: Boolean and conditional operators][expressions].

### 13. [Intermediate] How do shifts work, including negative numbers and large distances?

**Answer:** `>>` propagates the sign bit; `>>>` shifts in zero bits. Both operate
on the promoted left operand. Only the low five distance bits are used for an
`int`, and the low six for a `long`, so shifting an `int` by 32 is shifting by
zero. Do not implement range checks, division, or hashing with shifts unless
their exact signed behavior matches the requirement.

```java
assert (-8 >> 1) == -4;
assert (-1 >>> 1) == Integer.MAX_VALUE;
assert (1 << 32) == 1;
assert (1L << 32) == 4_294_967_296L;
```

**Reference:** [JLS 15.19: Shift operators][expressions].

### 14. [Intermediate] What evaluation order does Java guarantee?

**Answer:** Java evaluates operands and method arguments left to right, subject
to short-circuit and conditional-expression rules. Post-increment returns the
old value before updating; pre-increment updates first. These rules make
output predictable, but expressions that repeatedly mutate one variable are
hard to review. Rewrite them into explicit steps in production code.

```java
int i = 1;
int sum = i++ + ++i;
assert sum == 4 && i == 3;
int[] values = {10, 20};
int index = 0;
values[index++] += 5;
assert index == 1 && values[0] == 15;
```

**Reference:** [JLS 15.7: Evaluation order][expressions].

### 15. [Intermediate] Can the conditional operator unexpectedly unbox a null wrapper?

**Answer:** Yes. The type of a conditional expression follows language rules,
not simply "whichever branch ran." A numeric conditional mixing an `Integer`
and an `int` may have primitive type `int`. If its selected wrapper branch is
null, unboxing fails. Keep both branches explicitly reference-typed when you
intend to preserve absence, or use an ordinary conditional statement.

```java
Integer missing = null;
boolean useMissing = true;
// Would throw NullPointerException:
// Integer bad = useMissing ? missing : 0;
Integer safe = useMissing ? missing : Integer.valueOf(0);
assert safe == null;
```

**Reference:** [JLS 15.25: Conditional operator][expressions].

## B. Methods, overloads, overriding, and dispatch

### 16. [Beginner] What is the difference between overloading and overriding?

**Answer:** Overloading provides different parameter lists under one method
name; selection starts from compile-time types. Overriding replaces an
inherited instance method implementation; runtime dispatch selects the
implementation for the actual object. These are separate stages. A subclass
overload does not retroactively change the overload selected by a caller whose
reference type is the superclass.

```java
class Parent {
    String describe(Object x) { return "parent-object"; }
}
class Child extends Parent {
    @Override String describe(Object x) { return "child-object"; }
    String describe(String x) { return "child-string"; }
}
Parent value = new Child();
assert value.describe("hello").equals("child-object");
```

**References:** [JLS 8.4.8-8.4.9][classes]; [JLS 15.12][expressions].

### 17. [Beginner] What is a method signature? Can return type alone distinguish overloads?

**Answer:** A method signature involves its name, type parameters, and formal
parameter types under the JLS's adaptation rules. Return type, access modifier,
and `throws` clause do not independently distinguish overloads. Two methods
with the same parameter types and different return types cannot coexist.
Give operations different names when their meanings differ rather than
trying to force the distinction into return type.

```java
class Parser {
    int parse(String text) { return Integer.parseInt(text); }
    int parse(char[] text) { return parse(new String(text)); }
    // Does not compile: long parse(String text) { return 1L; }
}
assert new Parser().parse("42") == 42;
```

**Reference:** [JLS 8.4.2: Method signature][classes].

### 18. [Intermediate] Which wins in overload resolution: widening, boxing, or varargs?

**Answer:** The compiler first searches fixed-arity methods using strict
invocation, without boxing or unboxing. If none apply, it allows loose
invocation, including boxing/unboxing. Only then does it try variable-arity
invocation. Thus a primitive widening overload can beat a boxing overload.
Within a phase, the most-specific rules still matter; the slogan "widening
beats boxing" is not a complete overload-resolution algorithm.

```java
class Pick {
    String choose(long n) { return "long"; }
    String choose(Integer n) { return "Integer"; }
    String choose(int... n) { return "varargs"; }
}
assert new Pick().choose(1).equals("long");
assert new Pick().choose().equals("varargs");
```

**Reference:** [JLS 15.12.2: Three overload-resolution phases][expressions].

### 19. [Intermediate] What happens when null matches multiple overloads?

**Answer:** Null can convert to reference types, so the compiler looks for the
most specific applicable overload. `String` is more specific than `Object`.
For unrelated types such as `String` and `Integer`, neither is more specific,
so a bare-null call is ambiguous. Explicit casts can disambiguate a call, but
an API that routinely needs them may have an avoidable overload design problem.

```java
class Pick {
    String choose(Object x) { return "object"; }
    String choose(String x) { return "string"; }
}
assert new Pick().choose(null).equals("string");
class Ambiguous {
    void choose(String x) {}
    void choose(Integer x) {}
}
// Does not compile: new Ambiguous().choose(null);
```

**Reference:** [JLS 15.12.2.5: Most-specific method][expressions].

### 20. [Intermediate] Is `int` to `Long` allowed because Java can widen and box?

**Answer:** Not through that combination. Method invocation can box `int` to
`Integer` and then widen the reference to `Number` or `Object`; it does not
first widen `int` to `long` and then box to `Long`. Unboxing followed by primitive
widening is allowed. Reason from the permitted conversion chains rather than
assuming any individually valid conversions can be combined.

```java
class Receiver {
    void takeLong(Long value) {}
    long widen(Integer value) { return value; }
}
Receiver r = new Receiver();
r.takeLong(1L);
assert r.widen(Integer.valueOf(1)) == 1L;
// Does not compile: r.takeLong(1);
```

**Reference:** [JLS 5.3: Invocation contexts][conversions].

### 21. [Intermediate] Are static methods overridden? Can a null reference call one?

**Answer:** Static methods are hidden, not overridden. Selection depends on
the compile-time qualifying type. Java permits static access through an
expression, even one whose value is null, because the value is discarded;
evaluation of that expression can still have side effects. Write
`TypeName.method()` instead so the call does not misleadingly resemble dynamic
dispatch.

```java
// Class members
static class Parent { static String name() { return "parent"; } }
static class Child extends Parent { static String name() { return "child"; } }
static void example() {
    Parent reference = new Child();
    assert reference.name().equals("parent");
    Parent missing = null;
    assert missing.name().equals("parent");
}
```

**Reference:** [JLS 8.4.8.2 and 15.12.4.1][expressions].

### 22. [Intermediate] Can private or final methods be overridden?

**Answer:** A private method is not inherited as an overridable method.
A subclass method with the same spelling is a separate declaration.
A final instance method cannot be overridden; a final class cannot be
subclassed. Use `@Override` to ask the compiler to check your intention.
Do not confuse a final method with an immutable object: final restricts
dispatch or inheritance, not all changes to state.

```java
class Parent {
    private String secret() { return "parent"; }
    String reveal() { return secret(); }
    final int version() { return 1; }
}
class Child extends Parent {
    String secret() { return "child"; }
}
assert new Child().reveal().equals("parent");
```

**Reference:** [JLS 8.4.3.3 and 8.4.8][classes].

### 23. [Intermediate] What are covariant return types, and what else may an override change?

**Answer:** An override may return a more specific reference type. It may widen
access but not make it less accessible, and it may narrow or remove checked
exceptions but not add unrelated broader checked exceptions. Primitive return
types do not support covariance. These restrictions protect callers that rely
on the superclass method's contract; matching a name alone is not enough.

```java
class Parent {
    protected Number amount() throws IOException { return 1; }
}
class Child extends Parent {
    @Override public Integer amount() { return 2; }
}
assert new Child().amount().intValue() == 2;
```

**Reference:** [JLS 8.4.8.3: Overriding requirements][classes].

### 24. [Intermediate] Are fields polymorphic like instance methods?

**Answer:** No. A field access uses the declared type of its qualifying
expression. An overridden instance method uses runtime dispatch. Hiding fields
with the same name is legal but confusing because one object can contain both
fields. Prefer private fields with behavior-oriented accessors rather than
using public field hiding as a variation mechanism.

```java
class Parent {
    String label = "parent-field";
    String label() { return "parent-method"; }
}
class Child extends Parent {
    String label = "child-field";
    @Override String label() { return "child-method"; }
}
Parent value = new Child();
assert value.label.equals("parent-field");
assert value.label().equals("child-method");
```

**Reference:** [JLS 15.11.1: Field access][expressions].

### 25. [Beginner] What is the difference between `this` and `super`?

**Answer:** `this` identifies the current instance; `super` selects superclass
members or invokes a superclass constructor. `super.method()` deliberately
uses the superclass implementation instead of normal overriding dispatch for
that invocation. Neither is available as an instance reference in a static
context. Use `this.field` to distinguish a field from a parameter with the same
name, not as a substitute for clear naming.

```java
class Parent {
    String describe() { return "base"; }
}
class Child extends Parent {
    private final String label;
    Child(String label) { this.label = label; }
    @Override String describe() { return super.describe() + ":" + this.label; }
}
assert new Child("x").describe().equals("base:x");
```

**Reference:** [JLS 15.8.3 and 15.12][expressions].

### 26. [Intermediate] What are varargs, and is their parameter ever null?

**Answer:** A varargs parameter is an array parameter at the declaration level.
Callers can supply individual arguments or an existing array. Passing no
arguments creates an empty array; passing a cast null array supplies null.
Only the last parameter can be varargs. Decide whether null arrays are valid
and document it rather than assuming every call receives a non-null array.

```java
class Counter {
    int count(String... values) {
        return Objects.requireNonNull(values, "values").length;
    }
}
Counter counter = new Counter();
assert counter.count() == 0;
assert counter.count("a", "b") == 2;
// Would throw NullPointerException: counter.count((String[]) null);
```

**Reference:** [JLS 8.4.1 and 15.12.4.2][expressions].

### 27. [Advanced] Why can generic varargs cause heap pollution?

**Answer:** Arrays retain their runtime component type, while generic type
arguments are usually erased. A `List<String>...` parameter is consequently
backed by an array whose runtime checks cannot enforce each list's element
type. Treating it as `Object[]` and writing a `List<Integer>` can cause a later
cast failure. `@SafeVarargs` is a promise about implementation safety, not a
runtime repair. It is valid on static/final methods and constructors in Java 8,
and also private instance methods since Java 9.

```java
// Class members
@SafeVarargs
static <T> int totalSize(List<T>... groups) {
    int total = 0;
    for (List<T> group : groups) total += group.size();
    return total;
}
static void example() {
    assert totalSize(Arrays.asList("a"), Arrays.asList("b", "c")) == 3;
}
```

**Reference:** [JLS 8.4.1 and 9.6.4.7: Varargs and SafeVarargs][interfaces].

### 28. [Advanced] Can adding an overload break existing source code?

**Answer:** Yes. An overload can make a previously valid call ambiguous or
select a different method when recompiled. Existing class files generally keep
their previously selected symbolic method signature, so source and binary
compatibility are different questions. Review null arguments, lambdas, method
references, and numeric conversions when evolving public overloaded APIs.
Distinct operation names can be safer.

```java
class Api {
    String lookup(String id) { return "id:" + id; }
    // Adding lookup(Integer id) would make lookup(null) ambiguous.
}
assert new Api().lookup(null).equals("id:null");
```

**References:** [JLS 13: Binary compatibility][compatibility]; [JLS 15.12][expressions].

### 29. [Intermediate] Does Java support multiple dispatch based on all runtime argument types?

**Answer:** Ordinary method invocation dynamically dispatches on the receiver,
not on every argument's runtime class. Argument overload selection uses
compile-time types. If behavior must depend on two independently varying
types, use an explicit strategy, visitor/double-dispatch design, or clear
conditional logic. Do not expect a `String` object stored as `Object` to select
the `String` overload.

```java
class Printer {
    String print(Object value) { return "object"; }
    String print(String value) { return "string"; }
}
Object value = "hello";
assert new Printer().print(value).equals("object");
assert new Printer().print((String) value).equals("string");
```

**Reference:** [JLS 15.12: Method invocation][expressions].

### 30. [Intermediate] Does `final` on a method parameter protect the caller's object?

**Answer:** No. It prevents reassignment of the parameter variable inside the
method. It neither changes pass-by-value semantics nor prevents mutation of the
referenced object. Use immutable types or defensive copying when the object
must be protected. A final parameter can communicate local intent, but it is
not an ownership or thread-safety boundary.

```java
class Editor {
    void edit(final StringBuilder value) {
        value.append("!");
        // Does not compile: value = new StringBuilder();
    }
}
StringBuilder value = new StringBuilder("hello");
new Editor().edit(value);
assert value.toString().equals("hello!");
```

**Reference:** [JLS 4.12.4: Final variables][types].

## C. Classes, interfaces, construction, and access

### 31. [Beginner] When should you use a concrete class, abstract class, or interface?

**Answer:** A concrete class can be instantiated and supplies the required
implementation. An abstract class cannot be instantiated directly and can share
state, constructors, and implementation. An interface describes a role that
unrelated classes can implement; a class can implement multiple interfaces
but extends only one class. Choose based on substitutability and state
ownership, not the slogan that one is always better.

```java
// Class members
interface Priced { int cents(); }
abstract static class Product implements Priced {
    final String name;
    Product(String name) { this.name = Objects.requireNonNull(name); }
}
static final class Book extends Product {
    Book() { super("Java"); }
    public int cents() { return 2500; }
}
static void example() { assert new Book().cents() == 2500; }
```

**References:** [JLS 8][classes]; [JLS 9][interfaces].

### 32. [Intermediate] Can an abstract class have constructors, concrete methods, and no abstract methods?

**Answer:** Yes to all three. Its constructors establish the superclass part
of each subclass instance. Marking a class abstract prohibits direct
instantiation even if every method has an implementation. This can express
"use a specialized subtype," but unnecessary abstract base classes create
coupling. Prefer one only when there is a real shared contract or lifecycle.

```java
abstract class Named {
    private final String name;
    Named(String name) { this.name = Objects.requireNonNull(name); }
    String name() { return name; }
}
class Worker extends Named {
    Worker() { super("worker"); }
}
assert new Worker().name().equals("worker");
```

**Reference:** [JLS 8.1.1.1: Abstract classes][classes].

### 33. [Beginner] What are Java's access levels, including the protected-access trap?

**Answer:** `public` permits access when the enclosing type and, where
applicable, module are accessible. Package-private permits access within the
package. `private` access is allowed within the enclosing top-level nest.
`protected` includes package access and restricted subclass access outside the
package. In a cross-package subclass, accessing a protected instance member
through an arbitrary superclass reference is not generally permitted.

```java
// Class members
static class Account {
    private int balance;
    int packageValue() { return balance; }
    protected int subclassValue() { return balance; }
    public int value() { return balance; }
}
static void example() { assert new Account().value() == 0; }
```

The snippet demonstrates declarations, not the cross-package restriction.
To test that restriction, place a subclass in another package and compare
`this.subclassValue()` with access through an `Account`-typed parameter.

**Reference:** [JLS 6.6, especially 6.6.2: Protected access][names].

### 34. [Intermediate] When does the compiler provide a default constructor?

**Answer:** For an ordinary class with no constructor declarations, the compiler
provides a default constructor. Once you declare any constructor, it does not
add a no-argument one automatically. The generated default constructor must
be able to call an accessible superclass no-argument constructor. A
no-argument constructor written by you is not the JLS's "default constructor."

```java
class Empty {}
class Named {
    Named(String name) { Objects.requireNonNull(name); }
}
new Empty();
new Named("worker");
// Does not compile: new Named();
```

**Reference:** [JLS 8.8.9: Default constructor][classes].

### 35. [Intermediate] In what order are fields, initializer blocks, and constructors executed?

**Answer:** In the traditional Java 8-21 construction pattern, the object first
has default field values; superclass construction precedes the subclass's
instance field initializers and initializer blocks, which run in textual order,
followed by the rest of the subclass constructor. Static initialization is
separate and happens once per class initialization. Java 25 permits a restricted
constructor prologue before `super`; see question 38 rather than assuming the
old "first statement" rule is universal.

```java
StringBuilder order = new StringBuilder();
class Parent {
    Parent() { order.append("P"); }
}
class Child extends Parent {
    int value = initialize();
    int initialize() { order.append("F"); return 1; }
    { order.append("I"); }
    Child() { order.append("C"); }
}
new Child();
assert order.toString().equals("PFIC");
```

**References:** [JLS 12.5: Creation of instances][execution]; [JEP 513][constructors25].

### 36. [Advanced] Why should constructors avoid calling overridable methods?

**Answer:** Dynamic dispatch still applies during construction. A superclass
constructor can call a subclass override before the subclass's normal field
initializers have run. The override can observe zero or null instead of the
intended value. Make construction helpers private/final or move extensible work
to a separate lifecycle operation. Java 25's new constructor options do not
make arbitrary callback-based construction a sound default.

```java
class Parent {
    Parent() { check(); }
    void check() {}
}
class Child extends Parent {
    String name = "ready";
    String observed;
    @Override void check() { observed = name; }
}
Child child = new Child();
assert child.observed == null;
assert child.name.equals("ready");
```

**Reference:** [JLS 12.5: Dispatch during initialization][execution].

### 37. [Intermediate] What do `this(...)` and `super(...)` do in a constructor?

**Answer:** `this(...)` delegates to another constructor of the same class;
`super(...)` invokes a superclass constructor. Constructor chains must not be
recursive. In Java 8-21, an explicit constructor invocation must be first, so
validation often goes into the delegated constructor or a static argument
helper. Centralize validation so one overload cannot bypass an invariant.
Java 25 relaxes placement under restricted early-construction rules.

```java
class Port {
    final int number;
    Port() { this(8080); }
    Port(int number) {
        if (number < 1 || number > 65535) throw new IllegalArgumentException("port");
        this.number = number;
    }
}
assert new Port().number == 8080;
```

**References:** [JLS 8.8.7.1][classes]; [JEP 513][constructors25].

### 38. [Advanced] What changes for constructors in Java 25?

**Answer:** Flexible constructor bodies are final in Java 25. A constructor
can validate or compute arguments before an explicit `super(...)` or `this(...)`.
The prologue is an early construction context: it cannot freely use the
incomplete instance. Certain field assignments are allowed, but reading
instance state or calling instance methods is restricted. Do not generalize
this to "anything can run before super." The feature was previewed in Java
22-24; it is unavailable in Java 8, 11, 17, and 21.

```java
// Java 25+
// Class members
static class Parent {
    final int value;
    Parent(int value) { this.value = value; }
}
static class Child extends Parent {
    Child(int value) {
        if (value < 0) throw new IllegalArgumentException("negative");
        super(value);
    }
}
static void example() { assert new Child(3).value == 3; }
```

**Reference:** [JEP 513: Flexible constructor bodies][constructors25].

### 39. [Intermediate] What triggers class initialization, and do constants trigger it?

**Answer:** Active uses include creating an instance, invoking a declared static
method, and reading or writing a declared static field that is not a constant
variable. A compile-time constant can be inlined into the caller, so reading it
does not necessarily initialize its declaring class. Loading and initialization
are different phases. A `static final` reference to a newly constructed object
is not a constant variable.

```java
// Class members
static class Config {
    static final int LIMIT = 10;
    static final Integer BOXED = 10;
    static { System.out.println("initialized"); }
}
static void example() {
    assert Config.LIMIT == 10;
    assert Config.BOXED == 10; // This active use initializes Config.
}
```

**Reference:** [JLS 12.4.1: Initialization triggers][execution].

### 40. [Intermediate] Which interface members are allowed in Java 8 versus later versions?

**Answer:** Java 8 interfaces support abstract instance methods, default methods,
static methods, and fields that are implicitly `public static final`.
Private interface methods arrived in Java 9 to share implementation among
defaults and static helpers. Interfaces have no per-instance fields or
constructors. A constant reference can still point to a mutable object, so
publishing collections as interface "constants" can expose shared mutable state.

```java
// Java 9+
// Class members
interface Label {
    default String text() { return normalize(" java "); }
    private String normalize(String value) { return value.trim(); }
}
static void example() {
    Label label = new Label() {};
    assert label.text().equals("java");
}
```

**Reference:** [JLS 9.3-9.4: Fields and interface methods][interfaces].

### 41. [Advanced] How are conflicting default methods resolved?

**Answer:** A superclass method takes priority over interface defaults; a more
specific interface default takes priority over a less specific one.
Unrelated inherited defaults with the same signature require an explicit
override. A superclass's abstract declaration can also force the subclass to
implement the method rather than silently use a default. Use
`InterfaceName.super.method()` where legal to select a direct superinterface's
default implementation.

```java
// Class members
interface Left { default String name() { return "left"; } }
interface Right { default String name() { return "right"; } }
static class Both implements Left, Right {
    @Override public String name() { return Left.super.name() + "+" + Right.super.name(); }
}
static void example() { assert new Both().name().equals("left+right"); }
```

**Reference:** [JLS 8.4.8.4 and 9.4.1.3][interfaces].

### 42. [Beginner] What is encapsulation, beyond making fields private?

**Answer:** Encapsulation controls how callers can observe and change an
object's representation. A private field with an unrestricted setter may
still expose every invalid state. Design operations around invariants:
`withdraw(amount)` can check amount and balance together, unlike unrelated
setters. Do not return mutable internal objects unless shared ownership is
intentional and documented.

```java
class Wallet {
    private int cents = 100;
    void withdraw(int amount) {
        if (amount <= 0 || amount > cents) throw new IllegalArgumentException("amount");
        cents -= amount;
    }
    int balance() { return cents; }
}
Wallet wallet = new Wallet();
wallet.withdraw(30);
assert wallet.balance() == 70;
```

**References:** [JLS 6.6: Access control][names]; [JLS 8.3: Fields][classes].

### 43. [Intermediate] How do you choose between composition and inheritance?

**Answer:** Use inheritance when the subtype genuinely preserves the base
contract and the base was designed for extension. Use composition to reuse a
capability without inheriting every operation or lifecycle assumption.
Composition allows independent replacement and testing of collaborators.
Inheritance is not automatically wrong, but inheriting only to reuse a few
methods often creates a misleading "is-a" relationship.

```java
// Class members
interface Formatter { String format(String input); }
static final class Report {
    private final Formatter formatter;
    Report(Formatter formatter) { this.formatter = Objects.requireNonNull(formatter); }
    String render(String input) { return formatter.format(input); }
}
static void example() {
    Report report = new Report(value -> "[" + value + "]");
    assert report.render("ok").equals("[ok]");
}
```

**Reference:** [JLS 8.1.4-8.1.5: Superclasses and interfaces][classes].
The choice is a design trade-off, not a requirement imposed by the JLS.

### 44. [Advanced] What does the Liskov Substitution Principle mean in a Java API?

**Answer:** A caller using a base type should not need subtype-specific
exceptions to its valid assumptions. A subtype should not require stronger
inputs or provide weaker guarantees. Java's compiler enforces some signature
rules, but not behavioral promises such as "accepts every non-negative value."
Review documented preconditions, postconditions, error behavior, and mutation,
not just whether `extends` compiles.

```java
// Class members
interface Discount {
    // Contract: accepts all non-negative totals; result is between 0 and total.
    int apply(int total);
}
static class NoDiscount implements Discount {
    public int apply(int total) {
        if (total < 0) throw new IllegalArgumentException("total");
        return total;
    }
}
static void example() { assert new NoDiscount().apply(0) == 0; }
```

**Reference:** [JLS 8.4.8.3: Compiler-enforced override constraints][classes].
The behavioral principle goes beyond those compiler checks.

### 45. [Advanced] What is constructor escape, and why is publishing `this` dangerous?

**Answer:** Constructor escape occurs when another object can obtain the
instance before construction finishes, for example through a registry,
listener, or callback. A synchronous callback can already observe incomplete
state; another thread introduces additional publication concerns.
Create the object fully, then register or start it. A final field is not a
license to publish the reference from the constructor.

```java
class Component {
    final String name;
    Component(String name) { this.name = Objects.requireNonNull(name); }
    void register(Consumer<Component> registry) { registry.accept(this); }
}
Component component = new Component("ready");
component.register(value -> { assert value.name.equals("ready"); });
```

**References:** [JLS 12.5][execution]; [JLS 17.5: Final field semantics][memory].

## D. Equality, hashing, ordering, and Object contracts

### 46. [Beginner] When should you use `==`, `equals`, or `Objects.equals`?

**Answer:** Use primitive `==` for primitive comparison and reference `==` when
identity is the question. Use `equals` for a type's logical equality, but only
when the receiver is non-null. `Objects.equals(a, b)` handles nulls.
The default `Object.equals` is identity equality; a class does not gain
field-by-field equality automatically just because it has fields.

```java
String a = new String("java");
String b = new String("java");
assert a != b;
assert a.equals(b);
assert Objects.equals(null, null);
assert !Objects.equals(a, null);
```

**Reference:** [Object API: equals][object].

### 47. [Intermediate] What is the equals contract, and how should a value class implement it?

**Answer:** Equality must be reflexive, symmetric, transitive, and consistent
while relevant state stays unchanged; a non-null object must not equal null.
Compare the fields that define the value, and define the class/subclass policy
explicitly. A final value class makes that policy simpler. Equality should not
depend on a network call, changing clock, or mutable external service.

```java
class Code {
    final String value;
    Code(String value) { this.value = Objects.requireNonNull(value); }
    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return value.equals(((Code) other).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
}
assert new Code("A").equals(new Code("A"));
```

**Reference:** [Object API: equals and hashCode][object].

### 48. [Intermediate] What is the hashCode contract? Must different objects have different hashes?

**Answer:** Equal objects must have equal hash codes. Unequal objects may
collide; uniqueness is impossible for arbitrary objects mapped to a 32-bit
integer. A hash code need not be a memory address or stay the same between
executions. Compute it from equality-relevant state. Hash quality affects
performance, but correctness starts with consistency with equality.

```java
String a = "FB";
String b = "Ea";
assert !a.equals(b);
assert a.hashCode() == b.hashCode();
Set<String> values = new HashSet<>();
values.add(a);
values.add(b);
assert values.size() == 2;
```

**Reference:** [Object API: hashCode][object].

### 49. [Intermediate] What breaks if equals is overridden but hashCode is not?

**Answer:** Two logically equal instances may have different inherited hash
codes. A hash-based collection may search a different bucket and fail to find
an equal object. This is a contract violation, not a guarantee that every test
will fail: accidental collisions or reusing the same instance can mask it.
Test equality and hashing together using independently constructed values.

```java
class Key {
    final int id;
    Key(int id) { this.id = id; }
    @Override public boolean equals(Object other) {
        return other instanceof Key && id == ((Key) other).id;
    }
    @Override public int hashCode() { return Integer.hashCode(id); }
}
Set<Key> keys = new HashSet<>();
keys.add(new Key(7));
assert keys.contains(new Key(7));
```

**Reference:** [Object API: equals/hashCode relationship][object].

### 50. [Advanced] Should equals use instanceof or getClass?

**Answer:** Neither is universally correct. `getClass` prevents equality across
different runtime classes, which can simplify strict value semantics.
`instanceof` permits subtype equality but requires a careful design if
subclasses add equality-relevant state; symmetry and transitivity can break.
Make value classes final when extension is unnecessary. Proxy/entity equality
needs a separate, framework-aware identity policy rather than copying a
generic recipe.

```java
final class CustomerId {
    final long value;
    CustomerId(long value) { this.value = value; }
    @Override public boolean equals(Object other) {
        return other instanceof CustomerId && value == ((CustomerId) other).value;
    }
    @Override public int hashCode() { return Long.hashCode(value); }
}
assert new CustomerId(7).equals(new CustomerId(7));
```

**Reference:** [Object API: Symmetry and transitivity requirements][object].

### 51. [Advanced] Why are mutable hash keys dangerous?

**Answer:** A collection places an entry using the key's hash and equality at
insertion. If those properties change while it is stored, later lookup or
removal may search using a different hash or no longer consider it equal.
Behavior becomes inconsistent with how the key was stored. Prefer immutable
identity values as keys; if a key must change, remove it before the change and
reinsert it afterward.

```java
Map<String, String> users = new HashMap<>();
String stableId = "customer-7";
users.put(stableId, "old name");
users.put(stableId, "new name");
assert users.get("customer-7").equals("new name");
```

**References:** [Map API: Mutable-key warning][map]; [Object hash contract][object].

### 52. [Intermediate] What are Comparable and Comparator, and why avoid subtraction?

**Answer:** `Comparable` defines a type's natural ordering. `Comparator`
provides an external ordering, allowing several views without changing the
type. Comparison must obey sign symmetry, transitivity, and consistency rules.
Returning `a - b` can overflow and reverse the intended order. Use comparison
methods or comparator combinators, and define null handling explicitly.

```java
int a = Integer.MIN_VALUE;
int b = 1;
assert a - b > 0;
assert Integer.compare(a, b) < 0;
Comparator<String> order = Comparator.comparingInt(String::length)
        .thenComparing(Comparator.naturalOrder());
assert order.compare("aa", "bb") < 0;
```

**Reference:** [Comparator API][comparator].

### 53. [Advanced] Must compareTo returning zero mean equals returns true?

**Answer:** Consistency with equality is strongly recommended, but not
universally required by `Comparable`. A sorted set treats comparison-zero
values as duplicates, while a hash set uses equality. `BigDecimal` is a
documented example: `2.0` and `2.00` compare numerically equal but are not equal
under its scale-sensitive `equals`. Decide what "same" means for the domain
before selecting a collection.

```java
BigDecimal a = new BigDecimal("2.0");
BigDecimal b = new BigDecimal("2.00");
assert a.compareTo(b) == 0;
assert !a.equals(b);
assert new HashSet<>(Arrays.asList(a, b)).size() == 2;
assert new TreeSet<>(Arrays.asList(a, b)).size() == 1;
```

**Reference:** [BigDecimal API: Equality and cohorts][decimal].

### 54. [Intermediate] How should arrays be compared, and what does an array's equals do?

**Answer:** Arrays inherit identity-based `Object.equals`; they do not
automatically compare elements. Use `Arrays.equals` for one-dimensional
primitive or reference arrays and `Arrays.deepEquals` for nested object-array
structures. Use matching hash helpers if array contents participate in a
value object's equality. These utilities do not turn mutable arrays into
stable keys.

```java
int[] a = {1, 2};
int[] b = {1, 2};
assert !a.equals(b);
assert Arrays.equals(a, b);
assert Arrays.hashCode(a) == Arrays.hashCode(b);
Object[] x = {new int[]{1}};
Object[] y = {new int[]{1}};
assert Arrays.deepEquals(x, y);
```

**Reference:** [Arrays API][arrays].

### 55. [Intermediate] What should toString contain, and is its default output an address?

**Answer:** `toString` should give a concise diagnostic representation, not
silently become a serialization format. The default uses the class name, `@`,
and a hexadecimal hash-code representation; it is not a promised memory
address. Exclude secrets, large graphs, and expensive lazy-loading behavior.
Use a stable explicit formatter when external callers need a durable format.

```java
class Login {
    final String user;
    final String password;
    Login(String user, String password) { this.user = user; this.password = password; }
    @Override public String toString() { return "Login[user=" + user + ", password=REDACTED]"; }
}
assert !new Login("alice", "secret123").toString().contains("secret123");
```

**Reference:** [Object API: toString][object].

## E. Immutability, copying, and ownership

### 56. [Beginner] What makes an object immutable? Is final enough?

**Answer:** An immutable object's observable value does not change after
construction. Final fields prevent reassignment of those fields, not mutation
of referenced objects. Establish valid state once, avoid mutation methods,
control subclassing, and prevent mutable state from escaping. Deep immutability
depends on the reachable values, not just the modifiers on the outer class.

```java
final class Tags {
    private final List<String> values;
    Tags(List<String> values) {
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
    }
    List<String> values() { return values; }
}
List<String> input = new ArrayList<>(Arrays.asList("java"));
Tags tags = new Tags(input);
input.add("changed");
assert tags.values().size() == 1;
```

**References:** [JLS 4.12.4][types]; [Collections unmodifiableList][collections].

### 57. [Intermediate] Why copy mutable inputs and outputs, rather than only one side?

**Answer:** Without an input copy, the caller can later mutate your internal
state through its original reference. Without an output copy, a getter can
leak your stored reference. Copying both sides establishes ownership of the
mutable representation. Immutable values such as `String` or `Instant` do not
need routine defensive copying.

```java
class Token {
    private final byte[] bytes;
    Token(byte[] bytes) { this.bytes = bytes.clone(); }
    byte[] bytes() { return bytes.clone(); }
}
byte[] input = {1, 2};
Token token = new Token(input);
input[0] = 9;
byte[] output = token.bytes();
output[1] = 9;
assert Arrays.equals(token.bytes(), new byte[]{1, 2});
```

**Reference:** [Object API: Array cloning and shallow copies][object].

### 58. [Intermediate] What is the difference between an unmodifiable view and an immutable snapshot?

**Answer:** An unmodifiable view prevents mutation through that view, but
changes through the backing collection can remain visible. A snapshot copies
the container so later source changes are not reflected. Neither necessarily
copies mutable elements. `List.copyOf`, available since Java 10, creates an
unmodifiable result and rejects null elements; it need not allocate a new object
when the input is already suitable.

```java
List<String> source = new ArrayList<>(Arrays.asList("a"));
List<String> view = Collections.unmodifiableList(source);
List<String> snapshot = Collections.unmodifiableList(new ArrayList<>(source));
source.add("b");
assert view.size() == 2;
assert snapshot.size() == 1;
```

**References:** [Collections API][collections]; [List.copyOf API][list].

### 59. [Intermediate] What is a shallow copy versus a deep copy?

**Answer:** A shallow copy duplicates the outer object but shares referenced
objects. A deep copy duplicates the relevant mutable reachable state. "Deep"
must be defined: a graph can contain cycles, shared nodes, or resources that
cannot meaningfully be copied. Prefer a copy policy that says exactly which
values are independent instead of promising an unspecified "deep clone."

```java
List<StringBuilder> original = new ArrayList<>();
original.add(new StringBuilder("a"));
List<StringBuilder> shallow = new ArrayList<>(original);
List<StringBuilder> deep = new ArrayList<>();
for (StringBuilder value : original) deep.add(new StringBuilder(value));
original.get(0).append("b");
assert shallow.get(0).toString().equals("ab");
assert deep.get(0).toString().equals("a");
```

**Reference:** [Object API: clone semantics][object].

### 60. [Advanced] How does Object.clone work, and why are copy constructors often clearer?

**Answer:** `Object.clone` performs a field-by-field shallow copy; it does not
normally invoke the class's constructors. The class generally must implement
`Cloneable`, a marker interface that does not itself declare a public `clone`
method. Mutable referenced state still needs explicit copying. Copy constructors
or factories can clearly validate state, choose a target type, and explain what
is shared, without inheriting clone's special protocol.

```java
class Point {
    final int x;
    final int y;
    Point(int x, int y) { this.x = x; this.y = y; }
    Point(Point other) { this(other.x, other.y); }
}
Point original = new Point(1, 2);
Point copy = new Point(original);
assert copy != original && copy.x == original.x;
```

**Reference:** [Object API: clone][object].

### 61. [Advanced] Should you validate before or after defensively copying an input?

**Answer:** When the input can change, validating one state and then storing
another creates a time-of-check/time-of-use problem. Copy into your owned
representation, then validate the copy you will retain. This does not make
an unsafely concurrently modified input collection magically safe to copy;
the API still needs an ownership or synchronization contract for that case.

```java
class Range {
    private final int[] bounds;
    Range(int[] input) {
        int[] copy = input.clone();
        if (copy.length != 2 || copy[0] > copy[1]) {
            throw new IllegalArgumentException("invalid range");
        }
        bounds = copy;
    }
    int start() { return bounds[0]; }
}
assert new Range(new int[]{1, 3}).start() == 1;
```

**Reference:** [JLS 10: Arrays and array cloning][arrayrules].
Copy-then-validate is the design consequence of retaining an owned snapshot.

### 62. [Intermediate] Can an immutable collection contain mutable objects?

**Answer:** Yes. Preventing structural changes does not freeze element state.
If callers can mutate an element, the enclosing value may change observably.
Use immutable element types, copy elements to immutable representations, or
document that only the collection structure is protected. Distinguish these
guarantees in review instead of calling every unmodifiable list "deeply
immutable."

```java
StringBuilder element = new StringBuilder("a");
List<StringBuilder> values = Collections.unmodifiableList(Arrays.asList(element));
element.append("b");
assert values.get(0).toString().equals("ab");
List<String> frozenValues = Collections.singletonList(element.toString());
element.append("c");
assert frozenValues.get(0).equals("ab");
```

**Reference:** [Collections API: Unmodifiable views][collections].

### 63. [Intermediate] When is a builder useful, and where should validation live?

**Answer:** A builder is useful for many optional parameters, readable call
sites, and assembling complex values. The constructed object must still
validate its complete state. A builder can be reused or modified, so the result
must copy mutable contents rather than retain the builder's mutable container.
For two required arguments, a constructor or factory is usually simpler.

```java
class Options {
    final int timeout;
    Options(int timeout) {
        if (timeout <= 0) throw new IllegalArgumentException("timeout");
        this.timeout = timeout;
    }
}
class Builder {
    int timeout = 1000;
    Builder timeout(int value) { timeout = value; return this; }
    Options build() { return new Options(timeout); }
}
assert new Builder().timeout(2000).build().timeout == 2000;
```

**Reference:** [JLS 8.8: Construction mechanisms][classes].
Builder selection is a design judgment, not a Java language requirement.

### 64. [Intermediate] When should a static factory replace or complement a constructor?

**Answer:** A named factory can explain intent, choose a subtype, reuse an
instance, and expose a narrower return type. Constructors remain simple and
discoverable for straightforward creation. A factory is especially helpful
when multiple same-typed parameters would otherwise be confusing.
Document whether callers may rely on identity; caching and interning are
implementation choices unless promised by the API.

```java
// Class members
static final class Percentage {
    final int value;
    private Percentage(int value) {
        if (value < 0 || value > 100) throw new IllegalArgumentException("percentage");
        this.value = value;
    }
    static Percentage ofWholePercent(int value) { return new Percentage(value); }
}
static void example() { assert Percentage.ofWholePercent(20).value == 20; }
```

**Reference:** [JLS 8.8: Constructors][classes].

### 65. [Advanced] Does immutability automatically solve every thread-safety issue?

**Answer:** Immutability removes races on the object's unchanging value, but
it does not make a mutable registry, cache, or compound workflow safe.
Construction and publication must respect the memory model; final-field
guarantees rely on correct construction. A thread-safe immutable value can
still be used in a lost-update algorithm when a shared reference is replaced
without coordination. Separate value safety from workflow atomicity.

```java
final class Balance {
    final int cents;
    Balance(int cents) { this.cents = cents; }
    Balance plus(int amount) { return new Balance(Math.addExact(cents, amount)); }
}
Balance old = new Balance(10);
Balance updated = old.plus(5);
assert old.cents == 10 && updated.cents == 15;
```

**Reference:** [JLS 17.5: Final-field semantics][memory].

## F. Strings, Unicode, and text processing

### 66. [Beginner] Why is String immutable, and does reassigning a String change it?

**Answer:** A `String`'s value cannot be changed through its public API.
Operations such as `replace` return a result instead of editing the original.
Reassignment changes which object a variable references. Immutability enables
safe sharing, stable value-based hashing, and reuse of literals. It does not
mean a variable that holds a string is automatically final.

```java
String original = "java";
String alias = original;
String changed = original.replace('j', 'J');
original = "different";
assert alias.equals("java");
assert changed.equals("Java");
assert original.equals("different");
```

**Reference:** [String API][string].

### 67. [Intermediate] What does the string pool do? Are all equal strings the same object?

**Answer:** Literals and string-valued constant expressions are interned.
`intern()` returns the canonical pooled instance for equal contents.
Ordinary runtime string creation does not guarantee identity with a literal.
Use content equality for text, not assumptions about allocation or pooling.
Interning every external input is not a general memory-optimization strategy;
measure retention and workload first.

```java
String literal = "java";
String constant = "ja" + "va";
String created = new String("java");
assert literal == constant;
assert literal != created;
assert literal == created.intern();
assert literal.equals(created);
```

**References:** [JLS 3.10.5: String literals][lexical]; [String.intern API][string].

### 68. [Intermediate] How does runtime concatenation differ from constant concatenation?

**Answer:** A string-valued constant expression is evaluated at compile time
and interned. An expression involving an ordinary runtime variable is not
the same constant expression. The compiler/JVM may implement concatenation
differently across releases; Java 9 introduced an `invokedynamic`-based strategy.
Do not count internal temporary allocations from source syntax alone.
Observable string contents are the stable contract.

```java
String part = "ja";
String runtime = part + "va";
final String constantPart = "ja";
String constant = constantPart + "va";
assert runtime.equals("java");
assert runtime != "java";
assert constant == "java";
```

**References:** [JLS 15.18.1][expressions]; [JEP 280: String concatenation][concat].

### 69. [Beginner] When should you choose String, StringBuilder, or StringBuffer?

**Answer:** Use `String` for values and ordinary readable expressions. Use a
local `StringBuilder` for repeated mutable construction, especially in loops.
`StringBuffer` synchronizes individual operations, but that does not make an
arbitrary multi-call protocol atomic. Sharing a builder across unrelated
requests is usually an ownership error, not an invitation to add random
synchronization.

```java
StringBuilder builder = new StringBuilder();
for (int i = 0; i < 3; i++) builder.append(i);
String result = builder.toString();
builder.append(3);
assert result.equals("012");
assert builder.toString().equals("0123");
```

**References:** [StringBuilder API][builder]; [StringBuffer API][buffer].

### 70. [Intermediate] Why can repeated string concatenation in a loop become expensive?

**Answer:** Each loop iteration must produce the new cumulative string value.
Repeatedly copying growing contents can make total work quadratic in output
length. A builder can grow a mutable buffer and produce one final string.
Compiler optimizations for a single expression do not justify assuming a
whole accumulation loop becomes optimal. Prefer the clearer builder, then
measure hot-path behavior rather than claiming exact allocation counts.

```java
StringBuilder csv = new StringBuilder();
for (int i = 1; i <= 3; i++) {
    if (i > 1) csv.append(',');
    csv.append(i);
}
assert csv.toString().equals("1,2,3");
```

**References:** [StringBuilder API][builder]; [JLS 15.18.1][expressions].

### 71. [Intermediate] Does String.length count characters visible to a user?

**Answer:** It counts UTF-16 code units. A supplementary Unicode code point
uses two `char` values. A user-perceived character, or grapheme cluster, can
contain multiple code points, so code-point count is still not always visual
character count. Specify whether a limit is in bytes, code units, code points,
or graphemes before implementing validation or truncation.

```java
String text = "\uD83D\uDE00";
assert text.length() == 2;
assert text.codePointCount(0, text.length()) == 1;
assert text.codePointAt(0) == 0x1F600;
```

**Reference:** [String API: UTF-16 indexing][string].

### 72. [Intermediate] How do you iterate or truncate text without splitting a surrogate pair?

**Answer:** Use code-point APIs when your operation is defined in Unicode
code points. Advance by `Character.charCount(codePoint)`, or use
`offsetByCodePoints` for a boundary. This preserves surrogate pairs for
well-formed text but does not keep a multi-code-point grapheme together.
For display-oriented truncation, use a grapheme-aware policy and test the
Unicode rules supported by your runtime.

```java
String text = "A\uD83D\uDE00B";
int end = text.offsetByCodePoints(0, 2);
String firstTwo = text.substring(0, end);
assert firstTwo.equals("A\uD83D\uDE00");
int count = 0;
for (int i = 0; i < text.length();) {
    int cp = text.codePointAt(i);
    i += Character.charCount(cp);
    count++;
}
assert count == 3;
```

**Reference:** [String API: Code-point operations][string].

### 73. [Intermediate] Why can getBytes and new String corrupt text across machines?

**Answer:** Bytes do not identify their own character encoding. Decoding
UTF-8 bytes with another charset produces different characters or replacement
values. Java 8/11/17 default charsets can vary by environment; Java 18
standardized UTF-8 for default-charset APIs, with console and configuration
caveats. Specify the protocol's charset explicitly even on newer JDKs.
Never use an unspecified default as a wire-format contract.

```java
String text = "caf\u00E9";
byte[] encoded = text.getBytes(StandardCharsets.UTF_8);
String decoded = new String(encoded, StandardCharsets.UTF_8);
assert decoded.equals(text);
assert encoded.length == 5;
```

**References:** [JEP 400: UTF-8 by default][utf8]; [String API][string].

### 74. [Intermediate] What is the difference between trim and strip?

**Answer:** `trim()` removes leading and trailing code units with values at
most U+0020. Java 11's `strip()` uses Java's Unicode-aware
`Character.isWhitespace` definition. That definition still does not mean every
Unicode space-like character; non-breaking space is an important counterexample.
Choose the rule required by your data format rather than assuming either
method is universal cleanup.

```java
// Java 11+
String emSpace = "\u2003hello\u2003";
assert !emSpace.trim().equals("hello");
assert emSpace.strip().equals("hello");
assert !"\u00A0hello\u00A0".strip().equals("hello");
```

**Reference:** [String API: trim, strip, isBlank][string].

### 75. [Intermediate] What are the traps in split?

**Answer:** `split` takes a regular expression, not a literal separator.
For example, `.` matches almost any character unless escaped.
The one-argument form discards trailing empty fields; use a negative limit to
preserve them. A positive limit caps the number of pieces and keeps the
remaining suffix in the last piece. Do not use a naive split as a CSV parser
when quoted fields or escaped delimiters are legal.

```java
assert "a,b,".split(",").length == 2;
assert "a,b,".split(",", -1).length == 3;
String[] literalDot = "a.b".split("\\.");
assert Arrays.equals(literalDot, new String[]{"a", "b"});
assert "a,b,c".split(",", 2)[1].equals("b,c");
```

**Reference:** [String API: split][string].

### 76. [Intermediate] How do replace and replaceAll differ?

**Answer:** `replace(CharSequence, CharSequence)` performs literal replacement.
`replaceAll` interprets the first argument as a regex and the replacement
using regex replacement rules, where dollar signs and backslashes have
special meanings. Use literal replacement when you want literal behavior.
When building regex operations from data, quote the pattern and replacement
using their respective helpers.

```java
assert "a.b".replace(".", "-").equals("a-b");
assert "a1b2".replaceAll("\\d", "#").equals("a#b#");
String replacement = "$5";
String result = "x".replaceAll("x", java.util.regex.Matcher.quoteReplacement(replacement));
assert result.equals("$5");
```

**Reference:** [String API: replace and replaceAll][string].

### 77. [Advanced] Why is default-locale case conversion dangerous for identifiers?

**Answer:** Case mapping depends on locale, and some mappings change string
length. A service that lowercases protocol keys using the machine's default
locale can behave differently after deployment. Use `Locale.ROOT` for
locale-neutral transformations where the protocol permits them. Human-language
sorting and searching need their own locale-aware rules; `equalsIgnoreCase`
is not a universal linguistic comparison algorithm.

```java
String key = "TITLE";
assert key.toLowerCase(Locale.ROOT).equals("title");
String turkish = "I".toLowerCase(Locale.forLanguageTag("tr"));
assert turkish.equals("\u0131");
assert !turkish.equals("i");
```

**Reference:** [String API: Locale-sensitive case conversion][string].

### 78. [Intermediate] Can visually identical strings be unequal?

**Answer:** Yes. A character can have composed and decomposed representations,
and different characters can look alike. `String.equals` compares code-unit
sequences; it does not normalize Unicode. For domains that require canonical
equivalence, select and document a normalization form. Normalization is not a
complete defense against look-alike identifiers and should not be applied
blindly to passwords or arbitrary binary-like text.

```java
String composed = "\u00E9";
String decomposed = "e\u0301";
assert !composed.equals(decomposed);
String normalized = java.text.Normalizer.normalize(
        decomposed, java.text.Normalizer.Form.NFC);
assert composed.equals(normalized);
```

**Reference:** [Normalizer API][normalizer].

### 79. [Intermediate] What happens when null participates in string conversion?

**Answer:** String concatenation converts a null reference to the text
`"null"`; calling an instance method on null throws. Overloads can change what
a seemingly similar expression means: `String.valueOf(null)` selects the
`char[]` overload and throws, whereas `String.valueOf((Object) null)` returns
`"null"`. Avoid letting implicit conversions turn missing domain data into
plausible-looking output.

```java
Object value = null;
assert ("value=" + value).equals("value=null");
assert String.valueOf((Object) null).equals("null");
// Would throw NullPointerException: String.valueOf(null);
```

**References:** [String API: valueOf overloads][string]; [JLS 15.18.1][expressions].

### 80. [Advanced] Case study: how would you design a username canonicalization policy?

**Answer:** First define whether usernames are case-sensitive, which scripts
and characters are allowed, and whether canonical equivalence matters.
Validate length in the chosen unit, normalize only under a documented rule,
and use a locale-neutral transformation where appropriate. Preserve a display
form separately from a canonical lookup form. Enforce uniqueness on the
canonical form in storage; string cleanup alone cannot solve concurrent
duplicate creation or confusable-identifier abuse.

```java
class Usernames {
    String canonical(String input) {
        String normalized = java.text.Normalizer.normalize(
                Objects.requireNonNull(input), java.text.Normalizer.Form.NFC);
        if (!normalized.matches("[A-Za-z0-9_]{3,30}")) {
            throw new IllegalArgumentException("username policy");
        }
        return normalized.toLowerCase(Locale.ROOT);
    }
}
assert new Usernames().canonical("Java_Lead").equals("java_lead");
```

This example deliberately adopts a narrow ASCII policy; it is not a universal
international-username implementation.

**References:** [Normalizer API][normalizer]; [String API][string].

## G. Exceptions and resource ownership

### 81. [Beginner] What are checked exceptions, unchecked exceptions, and Error?

**Answer:** Every thrown object is a `Throwable`. `RuntimeException` and
`Error` subclasses are unchecked. Other throwable classes are checked;
ordinary checked application exceptions usually extend `Exception`.
Checked means the compiler requires handling or declaration, not that recovery
is guaranteed. Unchecked does not mean harmless or uncatchable.
`Error` generally describes conditions ordinary application code is not
expected to recover from.

```java
class InvalidInput extends Exception {
    InvalidInput(String message) { super(message); }
}
class Validator {
    void validate(String value) throws InvalidInput {
        if (value == null) throw new InvalidInput("missing input");
    }
}
new Validator().validate("ok");
assert RuntimeException.class.getSuperclass() == Exception.class;
```

**Reference:** [JLS 11.1.1: Exception categories][exceptions].

### 82. [Beginner] How do throw and throws differ?

**Answer:** `throw` is a statement that transfers control using a particular
throwable object. `throws` is part of a method or constructor declaration,
describing exceptions that may propagate. Declaring a checked exception does
not force every execution to throw it. Throwing a null reference produces
`NullPointerException`; it does not mean "throw nothing."

```java
class Loader {
    String load(boolean available) throws IOException {
        if (!available) throw new IOException("not available");
        return "data";
    }
}
assert new Loader().load(true).equals("data");
```

**Reference:** [JLS 11.2: Compile-time exception checking][exceptions].

### 83. [Intermediate] When should an API use checked versus unchecked exceptions?

**Answer:** Choose according to what callers can meaningfully do and what the
API should force them to consider. A recoverable external failure may deserve
a checked exception; invalid arguments often use unchecked exceptions.
Neither policy should become a blanket rule for every layer.
Document failure meaning, preserve causes, and keep normal absence distinct
from exceptional failure. Do not use checked exceptions merely to advertise
that a method can fail.

```java
class PortParser {
    int parse(String value) {
        int port = Integer.parseInt(value);
        if (port < 1 || port > 65535) throw new IllegalArgumentException("port");
        return port;
    }
}
assert new PortParser().parse("8080") == 8080;
```

**Reference:** [JLS 11.2: Exception checking contract][exceptions].
Checked-versus-unchecked selection is an API design decision.

### 84. [Intermediate] In what order are catch clauses considered, and what is multi-catch?

**Answer:** The first matching catch handles the exception, so a subtype catch
must precede a broader catch that would already handle it. Multi-catch shares
one handler across unrelated alternatives; one alternative cannot be a subtype
of another. Its exception parameter cannot be reassigned. Avoid broad handlers
that convert all failures into the same default value.

```java
try {
    throw new java.io.EOFException("short input");
} catch (java.io.EOFException e) {
    assert e.getMessage().equals("short input");
} catch (IOException e) {
    throw new IllegalStateException("other I/O failure", e);
}
// Illegal multi-catch: catch (IOException | java.io.EOFException e)
```

**Reference:** [JLS 14.20: Try and catch][statements].

### 85. [Intermediate] What is exception chaining, and how is it different from suppressed exceptions?

**Answer:** A cause explains why a higher-level failure happened, such as a
domain load failure caused by I/O. A suppressed exception is an additional
failure that occurred while another exception already had priority, commonly
during resource cleanup. Preserve the original throwable, not only its
message, so diagnostics retain stack and type information.

```java
IOException cause = new IOException("disk unavailable");
IllegalStateException failure = new IllegalStateException("cannot load account", cause);
assert failure.getCause() == cause;
IOException cleanup = new IOException("close failed");
failure.addSuppressed(cleanup);
assert failure.getSuppressed()[0] == cleanup;
```

**Reference:** [Throwable API][throwable].

### 86. [Advanced] What happens if try and finally both return or throw?

**Answer:** Abrupt completion of `finally` replaces the pending return or
exception from `try`. Even a normal finally block can mutate an object already
chosen as a return value, because the reference is saved before cleanup.
Avoid returning or throwing casually from finally. Use try-with-resources
for closeable ownership because it preserves cleanup failures as suppressed
exceptions rather than accidentally discarding the primary failure.

```java
class Example {
    int number() {
        try { return 1; }
        finally { return 2; } // Deliberate demonstration of a bad pattern.
    }
}
assert new Example().number() == 2;
```

**Reference:** [JLS 14.20.2: Finally completion rules][statements].

### 87. [Intermediate] Can finally fail to execute? Should critical delivery depend on it?

**Answer:** It executes when control leaves the try under normal language
execution, including many exception paths. It is not guaranteed after process
termination, a crash, or loss of the machine. It also cannot execute if control
never leaves a non-terminating try body. Persist critical business work using
a durable protocol, not an assumption that an in-process cleanup block will
always run.

```java
List<String> events = new ArrayList<>();
try {
    events.add("work");
    throw new IllegalStateException("failed");
} catch (IllegalStateException e) {
    events.add("handled");
} finally {
    events.add("cleanup");
}
assert events.equals(Arrays.asList("work", "handled", "cleanup"));
```

**Reference:** [JLS 14.20.2][statements].

### 88. [Intermediate] How does try-with-resources choose close order and exception priority?

**Answer:** Resources are initialized left to right and closed in reverse
order. If the body throws, its exception remains primary and close failures
are suppressed. If the body succeeds but closing fails, the first closing
failure is primary and later closing failures are suppressed. This gives
deterministic cleanup without losing diagnostic information.

```java
List<String> closed = new ArrayList<>();
class Resource implements AutoCloseable {
    final String name;
    Resource(String name) { this.name = name; }
    public void close() { closed.add(name); }
}
try (Resource a = new Resource("a"); Resource b = new Resource("b")) {
    assert closed.isEmpty();
}
assert closed.equals(Arrays.asList("b", "a"));
```

**Reference:** [JLS 14.20.3: Try-with-resources][statements].

### 89. [Advanced] What if the second resource cannot be created, or a resource is null?

**Answer:** If initialization of a later resource fails, earlier successfully
initialized resources are closed. The failed initialization does not produce
a resource to close. A null resource variable is allowed and skipped during
closing. If initialization fails and cleanup also fails, cleanup failures are
suppressed on the initialization failure. Test partial initialization, not
only the happy path.

```java
List<String> events = new ArrayList<>();
class Resource implements AutoCloseable {
    Resource(boolean fail) {
        if (fail) throw new IllegalStateException("construction");
    }
    public void close() { events.add("closed"); }
}
try (Resource first = new Resource(false); Resource second = new Resource(true)) {
    throw new AssertionError("unreachable");
} catch (IllegalStateException e) {
    assert e.getMessage().equals("construction");
}
assert events.equals(Collections.singletonList("closed"));
```

**Reference:** [JLS 14.20.3: Resource initialization translation][statements].

### 90. [Intermediate] How do AutoCloseable and Closeable differ, and what changed in Java 9?

**Answer:** `AutoCloseable.close` can declare `Exception`; `Closeable.close`
narrows that to `IOException`. `Closeable` requires already-closed resources
to have no effect when closed again; `AutoCloseable` does not require
idempotence, though it is encouraged. Java 9 allows an existing final or
effectively final variable in a resource specification. Java 8 normally
declares a resource variable inside the try header.

```java
// Java 9+
StringReader reader = new StringReader("java");
try (reader) {
    assert reader.read() == 'j';
}
```

**References:** [AutoCloseable API][autocloseable]; [Closeable API][closeable];
[JLS 14.20.3][statements].

### 91. [Advanced] How should exceptions cross repository, domain, and API boundaries?

**Answer:** Translate only where the abstraction changes or useful context is
added. Preserve causes internally, expose stable error meanings externally,
and let one boundary own final logging and response mapping. Do not turn a
storage outage into "customer not found." Recovery, retries, and status
mapping depend on the failure category, so broad generic wrappers can remove
important information.

```java
class LoadFailure extends RuntimeException {
    LoadFailure(String id, IOException cause) {
        super("Cannot load customer " + id, cause);
    }
}
IOException lowLevel = new IOException("storage unavailable");
LoadFailure domain = new LoadFailure("C7", lowLevel);
assert domain.getCause() == lowLevel;
```

**Reference:** [Throwable API: Cause constructors][throwable].
Layer boundaries and logging ownership are application design decisions.

### 92. [Advanced] Why avoid catching Throwable or Error and continuing?

**Answer:** Serious failures such as linkage errors and out-of-memory
conditions may leave the operation unable to continue safely. Catching them
and returning an empty result falsely reports success. A carefully designed
process or task boundary may record a terminal failure, but that is different
from generic recovery. Catch the failures you understand, and let unexpected
defects remain visible.

```java
class Parser {
    int parse(String input) { return Integer.parseInt(input); }
}
try {
    new Parser().parse("bad");
    throw new AssertionError("expected failure");
} catch (NumberFormatException expected) {
    assert expected.getMessage() != null;
}
```

**Reference:** [JLS 11.1.1: Error versus Exception][exceptions].

### 93. [Intermediate] How should InterruptedException be handled?

**Answer:** Prefer propagating it when the method contract allows cancellation
to travel upward. If a boundary cannot propagate it directly, restore the
interrupt flag and report or terminate the operation appropriately.
Do not log and continue as though the work succeeded. Restoring the flag is
not a full cancellation design, but it preserves a signal that callers and
blocking operations may need.

```java
class CancellationBoundary {
    RuntimeException translate(InterruptedException cause) {
        Thread.currentThread().interrupt();
        return new IllegalStateException("operation interrupted", cause);
    }
}
RuntimeException failure = new CancellationBoundary()
        .translate(new InterruptedException("cancelled"));
assert Thread.currentThread().isInterrupted();
assert failure.getCause() instanceof InterruptedException;
Thread.interrupted(); // Clear only to isolate this demonstration.
```

**Reference:** [Thread API: Interrupt status][thread].

### 94. [Advanced] Why should finalizers not manage files, sockets, or locks?

**Answer:** Finalization has unpredictable timing and might never run.
Garbage collection concerns memory reachability, not prompt release of
external resources. `finalize` was deprecated in Java 9 and deprecated for
removal in Java 18; do not describe it as a dependable lifecycle hook.
Use explicit ownership with try-with-resources. A `Cleaner` can be a
specialized safety net, not the normal cleanup path.

```java
byte[] content = {1, 2, 3};
try (ByteArrayInputStream input = new ByteArrayInputStream(content)) {
    assert input.read() == 1;
}
```

This in-memory resource is harmless, but the ownership pattern also applies
to resources whose close operation releases OS handles.

**References:** [JEP 421: Deprecate finalization for removal][finalization];
[AutoCloseable API][autocloseable].

### 95. [Advanced] Case study: how do you test primary and suppressed cleanup failures?

**Answer:** Make both the body and cleanup fail deliberately. Assert that the
body failure is the thrown object and that the cleanup failure appears in
`getSuppressed()`. Also test successful work with failing close, multiple
failing resources, and partially initialized resources. Merely verifying
"an exception happened" misses the diagnostic contract that try-with-resources
is meant to preserve.

```java
class Resource implements AutoCloseable {
    public void close() throws IOException { throw new IOException("close"); }
}
try (Resource resource = new Resource()) {
    throw new IllegalArgumentException("body");
} catch (IllegalArgumentException e) {
    assert e.getMessage().equals("body");
    assert e.getSuppressed().length == 1;
    assert e.getSuppressed()[0].getMessage().equals("close");
}
```

**Reference:** [JLS 14.20.3: Suppression semantics][statements].

## H. Lambdas and functional API design

### 96. [Beginner] What is a functional interface? Must it have exactly one declared method?

**Answer:** It must have a single abstract-method contract under the JLS rules,
not literally one method declaration. Defaults and static methods do not add
abstract contracts. Public methods corresponding to `Object` methods are
excluded, and inherited override-equivalent abstract declarations can represent
one contract. `@FunctionalInterface` is optional but makes the compiler check
your intention.

```java
// Class members
@FunctionalInterface
interface Rule {
    boolean accepts(String input);
    default String description() { return "rule"; }
}
static void example() {
    Rule nonEmpty = value -> !value.isEmpty();
    assert nonEmpty.accepts("java");
}
```

**Reference:** [JLS 9.8: Functional interfaces][interfaces].

### 97. [Intermediate] What is a lambda's target type, and why can an overload be ambiguous?

**Answer:** A lambda is checked against an expected functional interface.
It does not have a standalone class type inferred solely from its syntax.
Overloads accepting unrelated functional interfaces with equivalent shapes
can become ambiguous. Prefer a local variable with an explicit target type,
or distinct method names if callers routinely need casts to resolve ambiguity.

```java
// Class members
interface TextFunction { String apply(String value); }
interface AlternateFunction { String apply(String value); }
static String use(TextFunction fn) { return fn.apply("x"); }
static String use(AlternateFunction fn) { return fn.apply("x"); }
static void example() {
    TextFunction fn = value -> value.toUpperCase(Locale.ROOT);
    assert use(fn).equals("X");
    // Does not compile: use(value -> value);
}
```

**References:** [JLS 15.27: Lambda expressions][expressions];
[Oracle lambda tutorial][lambdas].

### 98. [Intermediate] What can a lambda capture, and what does effectively final mean?

**Answer:** A captured local variable must be final or effectively final:
it is not reassigned after initialization under the definite-assignment rules.
The referenced object can still be mutable. This restriction does not make a
lambda thread-safe or freeze its reachable state. Prefer returning a result
over mutating a captured holder merely to bypass the restriction.

```java
String prefix = "hello ";
Function<String, String> greeting = name -> prefix + name;
assert greeting.apply("Java").equals("hello Java");
List<String> values = new ArrayList<>();
Runnable action = () -> values.add("changed");
action.run();
assert values.size() == 1;
// Reassigning prefix would make its lambda capture invalid.
```

**Reference:** [JLS 15.27.2: Lambda body capture rules][expressions].

### 99. [Intermediate] How does a lambda differ from an anonymous class, especially for this?

**Answer:** A lambda uses the enclosing lexical meaning of `this`; an
anonymous class has its own instance. Anonymous classes can extend classes
and declare additional state and methods; lambdas implement functional
interfaces. Do not rely on lambda identity or assume each evaluation allocates
a new instance. Choose based on the abstraction, not only on fewer characters.

```java
// Class members
static class Owner {
    final String name = "owner";
    Supplier<String> lambda() { return () -> this.name; }
    Supplier<String> anonymous() {
        return new Supplier<String>() {
            final String name = "anonymous";
            public String get() { return this.name; }
        };
    }
}
static void example() {
    Owner owner = new Owner();
    assert owner.lambda().get().equals("owner");
    assert owner.anonymous().get().equals("anonymous");
}
```

**Reference:** [JLS 15.27.2 and 15.27.4][expressions].

### 100. [Beginner] When do you use Predicate, Function, Consumer, Supplier, and UnaryOperator?

**Answer:** A `Predicate<T>` tests a value; `Function<T,R>` transforms it;
`Consumer<T>` performs an action; `Supplier<T>` provides a value without an
input; `UnaryOperator<T>` transforms a value into the same type.
Use the interface matching the operation's meaning. Primitive specializations
such as `IntPredicate` avoid wrapper-based signatures when the domain is
primitive-valued.

```java
Predicate<String> nonEmpty = value -> !value.isEmpty();
Function<String, Integer> length = String::length;
Supplier<StringBuilder> create = StringBuilder::new;
Consumer<StringBuilder> append = value -> value.append("x");
UnaryOperator<String> trim = String::trim;
StringBuilder text = create.get();
append.accept(text);
assert nonEmpty.test(text.toString());
assert length.apply(trim.apply(" xx ")) == 2;
```

**Reference:** [java.util.function package][functions].

### 101. [Intermediate] How do bound and unbound method references differ?

**Answer:** A bound reference such as `prefix::concat` captures a particular
receiver. An unbound reference such as `String::length` takes its receiver
from the functional interface's argument. Static and constructor references
have their own shapes. A bound instance reference evaluates its receiver when
the reference is created; a null receiver can therefore fail before the
function is invoked.

```java
String prefix = "hello ";
Function<String, String> bound = prefix::concat;
Function<String, Integer> unbound = String::length;
BiFunction<String, String, String> concat = String::concat;
assert bound.apply("Java").equals("hello Java");
assert unbound.apply("Java") == 4;
assert concat.apply("a", "b").equals("ab");
```

**Reference:** [JLS 15.13: Method references][expressions].

### 102. [Intermediate] What is the difference between compose and andThen?

**Answer:** `f.andThen(g)` applies `f` first and then `g`.
`f.compose(g)` applies `g` first and then `f`. Write the intermediate value
types when a composition is confusing. The functions must have compatible
types, and exceptions propagate according to the called functions; composition
does not provide an automatic transaction or rollback for side effects.

```java
Function<Integer, Integer> doubleIt = value -> value * 2;
Function<Integer, Integer> addThree = value -> value + 3;
assert doubleIt.andThen(addThree).apply(4) == 11;
assert doubleIt.compose(addThree).apply(4) == 14;
```

**Reference:** [Function API][function].

### 103. [Advanced] How do checked exceptions interact with standard functional interfaces?

**Answer:** A lambda can throw checked exceptions only when the target
function's abstract method permits them. `Function.apply` does not declare
`IOException`, so a file-reading lambda cannot simply propagate it.
Use an appropriate throwing interface or translate at a meaningful boundary,
preserving the cause. Never convert failures to empty strings just to satisfy
a functional signature.

```java
// Class members
@FunctionalInterface
interface IoSupplier<T> { T get() throws IOException; }
static String read(IoSupplier<String> supplier) throws IOException {
    return supplier.get();
}
static void example() throws IOException {
    assert read(() -> "content").equals("content");
}
```

**References:** [JLS 11.2.3][exceptions]; [Function API][function].

### 104. [Intermediate] How do orElse and orElseGet differ, and when is Optional useful?

**Answer:** `orElse(value)` evaluates its argument before the method call,
even when the optional is present. `orElseGet(supplier)` calls the supplier
only when empty. Use `Optional` mainly to express a possibly absent return
value; it does not replace every nullable field or parameter automatically.
Keep "absent" distinct from "operation failed," and never return null in place
of an optional.

```java
List<String> events = new ArrayList<>();
Supplier<String> fallback = () -> { events.add("computed"); return "fallback"; };
Optional<String> present = Optional.of("value");
assert present.orElse(fallback.get()).equals("value");
assert events.size() == 1;
assert present.orElseGet(fallback).equals("value");
assert events.size() == 1;
```

**Reference:** [Optional API][optional].

### 105. [Advanced] Case study: when should behavior be injected as a function rather than hard-coded?

**Answer:** Inject a function when a small, well-defined policy varies while
the surrounding workflow remains stable. Use a named interface when the
policy has domain meaning, multiple operations, or a richer contract.
Avoid exposing arbitrary callbacks without defining their error, mutation,
and invocation guarantees. A function is not automatically pure just because
its syntax is a lambda.

```java
class FeeCalculator {
    private final IntUnaryOperator policy;
    FeeCalculator(IntUnaryOperator policy) { this.policy = Objects.requireNonNull(policy); }
    int total(int base) {
        if (base < 0) throw new IllegalArgumentException("base");
        return Math.addExact(base, policy.applyAsInt(base));
    }
}
FeeCalculator calculator = new FeeCalculator(base -> base / 10);
assert calculator.total(1000) == 1100;
```

**Reference:** [java.util.function package][functions].
The policy contract must additionally define permitted fee values and overflow behavior.

## I. Annotations, reflection, nested types, and enums

### 106. [Beginner] What is an annotation, and does adding one enforce behavior?

**Answer:** An annotation is structured metadata. Its effect depends on a
compiler rule, processor, framework, or runtime reader. Writing `@Positive`
does not validate anything unless a consumer implements that behavior.
Use annotations for declarative contracts only when their enforcement path
is clear, and test that path rather than assuming the spelling itself does
the work.

```java
// Class members
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Audited {}
@Audited
static void operation() {}
static void example() throws Exception {
    assert Check106.class.getDeclaredMethod("operation").isAnnotationPresent(Audited.class);
}
```

Here `Check106` is the surrounding example class's name; replace it with your
own class name when copying the declarations.

**Reference:** [JLS 9.6-9.7: Annotation interfaces and annotations][interfaces].

### 107. [Intermediate] How do SOURCE, CLASS, and RUNTIME retention differ?

**Answer:** SOURCE annotations are discarded by compilation. CLASS annotations
are recorded in class files but not required to be available through runtime
reflection; CLASS is the default retention. RUNTIME annotations are retained
for reflective access. `@Target` controls where annotations may appear.
Retention does not determine whether a framework actually scans or acts on
the annotation.

```java
// Class members
@Retention(RetentionPolicy.CLASS)
@interface BuildMetadata {}
@Retention(RetentionPolicy.RUNTIME)
@interface RuntimeMetadata {}
@BuildMetadata @RuntimeMetadata
static class Marked {}
static void example() {
    assert !Marked.class.isAnnotationPresent(BuildMetadata.class);
    assert Marked.class.isAnnotationPresent(RuntimeMetadata.class);
}
```

**Reference:** [JLS 9.6.4.2-9.6.4.3: Retention and target][interfaces].

### 108. [Advanced] What do Inherited and Repeatable actually guarantee?

**Answer:** `@Inherited` applies to class annotations inherited through a
superclass chain. It does not generally propagate annotations from interfaces,
methods, or fields. `@Repeatable` allows multiple occurrences through a
container annotation; use `getAnnotationsByType` to retrieve repeated values.
Frameworks can implement broader scanning rules, but those are not the same
as Java's built-in inheritance semantics.

```java
// Class members
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@interface Role {}
@Role static class Parent {}
static class Child extends Parent {}
static void example() {
    assert Child.class.isAnnotationPresent(Role.class);
}
```

**Reference:** [JLS 9.6.3 and 9.6.4.4: Repeatable and inherited annotations][interfaces].

### 109. [Intermediate] How do annotation processing and runtime reflection differ?

**Answer:** Annotation processing runs during compilation and commonly generates
source files or diagnostics. Runtime reflection examines loaded types and
runtime-retained metadata while the application executes. Generated code may
avoid repeated runtime discovery, but introduces a build-time dependency.
Neither standard annotation processing nor reflection means "annotations
execute themselves." Know whether the behavior disappears when the build
processor or runtime framework is missing.

```java
// Class members
@Retention(RetentionPolicy.RUNTIME)
@interface Route { String value(); }
@Route("/health")
static class Health {}
static void example() {
    Route route = Health.class.getAnnotation(Route.class);
    assert route.value().equals("/health");
}
```

This snippet demonstrates runtime reading; an annotation processor instead
implements the compiler's processing API.

**References:** [javax.annotation.processing package][processors]; [JLS 9.7][interfaces].

### 110. [Intermediate] How do getMethods and getDeclaredMethods differ?

**Answer:** `getMethods()` returns public methods, including inherited ones.
`getDeclaredMethods()` returns methods declared by that type, including
non-public ones, but not inherited methods as such. Reflection results may
include synthetic or bridge methods and have no promised source order.
Specify parameter types to select an overload; searching only by method name
can select the wrong operation.

```java
class Parent { public void inherited() {} }
class Child extends Parent { private void local() {} }
assert Child.class.getMethod("inherited").getDeclaringClass() == Parent.class;
assert Child.class.getDeclaredMethod("local").getDeclaringClass() == Child.class;
```

**Reference:** [Class API][classapi].

### 111. [Advanced] How do reflective method invocation and access restrictions behave?

**Answer:** `Method.invoke` checks access and converts permitted arguments.
If the invoked body throws, reflection wraps the cause in
`InvocationTargetException`. Modern module boundaries can prevent deep access;
`setAccessible(true)` is not a universal bypass. Java 9 added APIs such as
`trySetAccessible`, and Java 17 strongly encapsulates JDK internals.
Prefer a public contract when reflection is not genuinely required.

```java
Method method = String.class.getMethod("substring", int.class);
assert method.invoke("java", 1).equals("ava");
try {
    method.invoke("java", 99);
    throw new AssertionError("expected failure");
} catch (InvocationTargetException e) {
    assert e.getCause() instanceof IndexOutOfBoundsException;
}
```

**References:** [Oracle reflection tutorial][reflection]; [JEP 403][encapsulation].

### 112. [Intermediate] What are static nested classes, inner classes, and local classes?

**Answer:** A static nested class has no implicit enclosing instance.
A non-static inner class is associated with an enclosing instance and can
access its state. A local class is declared inside a block and can capture
eligible local variables. Use a static nested class when it does not need
its owner's instance; otherwise an unnecessary enclosing reference can keep
a larger object graph reachable.

```java
class Outer {
    final int value = 7;
    class Inner { int read() { return value; } }
}
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
assert inner.read() == 7;
```

**Reference:** [JLS 8.1.3: Inner classes and enclosing instances][classes].

### 113. [Advanced] What is type erasure, and why might reflection show a bridge method?

**Answer:** Erasure maps generic types to runtime representations without most
type arguments. The compiler can generate a bridge method so an overriding
generic implementation still satisfies calls using the erased superclass or
interface signature. Reflection can reveal that synthetic method.
Do not count every reflected method as a source-declared business operation;
check `isBridge` and `isSynthetic` when building method scanners.

```java
class Names implements Comparable<Names> {
    @Override public int compareTo(Names other) { return 0; }
}
boolean bridgeFound = false;
for (Method method : Names.class.getDeclaredMethods()) {
    if (method.isBridge()) bridgeFound = true;
}
assert bridgeFound;
```

**References:** [JLS 4.6: Erasure][types]; [Method API][method].
Generic collections are explored in [Collections and Generics](collections-generics-and-data-structures.md).

### 114. [Beginner] What is an enum, and can it contain state and behavior?

**Answer:** An enum defines a fixed set of named instances. It can contain
fields, methods, constructors, and constant-specific behavior, and implement
interfaces. Its constructors are not public construction APIs.
Enum identity comparison with `==` is appropriate and null-safe when one
operand is a constant. Prefer enums to unchecked string constants for a closed
set of domain states.

```java
// Class members
enum Status {
    NEW(false), COMPLETE(true);
    private final boolean terminal;
    Status(boolean terminal) { this.terminal = terminal; }
    boolean terminal() { return terminal; }
}
static void example() {
    assert !Status.NEW.terminal();
    assert Status.COMPLETE.terminal();
    Status missing = null;
    assert missing != Status.COMPLETE;
}
```

**Reference:** [JLS 8.9: Enum classes][classes].

### 115. [Advanced] Why should ordinal not be a persistent enum identifier?

**Answer:** `ordinal()` is the declaration position, so inserting or reordering
constants changes it. Persisting `name()` is better only if renaming is
controlled; it is still an external contract. Prefer an explicit stable code
and a defined unknown-value policy. Adding an enum constant may also require
updating exhaustive dispatch and integrations, even if the basic Java type
remains compatible.

```java
// Class members
enum PaymentState {
    PENDING("P"), SETTLED("S");
    final String code;
    PaymentState(String code) { this.code = code; }
    static PaymentState fromCode(String code) {
        for (PaymentState state : values()) if (state.code.equals(code)) return state;
        throw new IllegalArgumentException("unknown payment state: " + code);
    }
}
static void example() { assert PaymentState.fromCode("S") == PaymentState.SETTLED; }
```

**Reference:** [Enum API: name and ordinal][enumapi].
