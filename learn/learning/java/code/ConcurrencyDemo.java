import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Four threads bump two counters. The plain int races; the AtomicInteger is safe.
public class ConcurrencyDemo {
    static int unsafe = 0;                                 // shared, unsynchronized
    static final AtomicInteger safe = new AtomicInteger(); // lock-free, correct

    public static void main(String[] args) throws Exception {
        Runnable bump = () -> {
            for (int i = 0; i < 100_000; i++) {
                unsafe++;                    // read-modify-write: NOT atomic -> lost updates
                safe.incrementAndGet();      // atomic -> never loses an update
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(4);
        Future<?>[] fs = new Future[4];
        for (int i = 0; i < 4; i++) fs[i] = pool.submit(bump);
        for (Future<?> f : fs) f.get();      // wait for all four
        pool.shutdown();

        System.out.println("expected      = " + (4 * 100_000));
        System.out.println("unsafe (race) = " + unsafe);       // usually LESS than expected
        System.out.println("safe (atomic) = " + safe.get());   // exactly expected
    }
}
