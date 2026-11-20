import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Lesson 01 — Dependency Injection with zero frameworks.
 *
 * Deliberately written for Java 8 so it runs anywhere:
 *   javac DiByHand.java && java DiByHand
 */
public class DiByHand {

    static final class Book {
        final long id; final String title; final String author;
        Book(long id, String title, String author) {
            this.id = id; this.title = title; this.author = author;
        }
        String title()  { return title; }
        String author() { return author; }
    }

    /** The abstraction the high-level code depends on. */
    interface BookRepository {
        Optional<Book> findById(long id);
    }

    static class InMemoryBookRepository implements BookRepository {
        private final Map<Long, Book> data = new HashMap<Long, Book>();
        InMemoryBookRepository() { data.put(1L, new Book(1, "Dune", "Herbert")); }
        public Optional<Book> findById(long id) { return Optional.ofNullable(data.get(id)); }
    }

    /** A test double. No mocking library required — this is what DI buys you. */
    static class AlwaysEmptyRepository implements BookRepository {
        public Optional<Book> findById(long id) { return Optional.empty(); }
    }

    /** Exercise from the lesson: a hand-written proxy. BookService never notices. */
    static class LoggingBookRepository implements BookRepository {
        private final BookRepository delegate;
        LoggingBookRepository(BookRepository delegate) { this.delegate = delegate; }
        public Optional<Book> findById(long id) {
            System.out.println("  [log] looking up " + id);
            return delegate.findById(id);
        }
    }

    /** High-level policy: knows nothing about which implementation it received. */
    static class BookService {
        private final BookRepository repo;          // final = set once, never null
        BookService(BookRepository repo) {          // <-- THE injection point
            this.repo = repo;
        }
        String describe(long id) {
            return repo.findById(id)
                       .map(b -> b.title() + " by " + b.author())
                       .orElse("not found");
        }
    }

    /** The "container": for now, just main() — the composition root. */
    public static void main(String[] args) {
        BookService real    = new BookService(new InMemoryBookRepository());
        BookService test    = new BookService(new AlwaysEmptyRepository());
        BookService audited = new BookService(new LoggingBookRepository(new InMemoryBookRepository()));

        System.out.println(real.describe(1));
        System.out.println(test.describe(1));
        System.out.println(audited.describe(1));
    }
}
