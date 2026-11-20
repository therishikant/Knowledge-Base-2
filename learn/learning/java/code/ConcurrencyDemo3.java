import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Part 2 of the concurrency cluster: visibility, atomicity, locks, CAS, deadlock avoidance.
public class ConcurrencyDemo3 {

    static boolean plainFlag = false;
    static volatile boolean volatileFlag = false;

    static volatile int volatileCounter = 0;
    static int guardedCounter = 0;
    static final Object LOCK = new Object();
    static final AtomicInteger atomicCounter = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        visibility();
        atomicity();
        casLoop();
        deadlockAvoidance();
    }

    // A. Does the reader thread ever SEE the writer's change?
    static void visibility() throws Exception {
        System.out.println("--- A. visibility ---");

        Thread plainReader = new Thread(() -> {
            long deadline = System.currentTimeMillis() + 2000;
            long spins = 0;
            while (!plainFlag && System.currentTimeMillis() < deadline) spins++;
            System.out.println("plain    reader saw the flag? " + plainFlag + "  (spins=" + spins + ")");
        });
        plainReader.start();
        Thread.sleep(100);
        plainFlag = true;
        plainReader.join();

        Thread volatileReader = new Thread(() -> {
            long deadline = System.currentTimeMillis() + 2000;
            while (!volatileFlag && System.currentTimeMillis() < deadline) { /* spin */ }
            System.out.println("volatile reader saw the flag? " + volatileFlag);
        });
        volatileReader.start();
        Thread.sleep(100);
        volatileFlag = true;
        volatileReader.join();
    }

    // B. volatile gives visibility but NOT atomicity; synchronized and atomics give both.
    static void atomicity() throws Exception {
        System.out.println("--- B. atomicity ---");
        Runnable bump = () -> {
            for (int i = 0; i < 100_000; i++) {
                volatileCounter++;                      // read-modify-write: still races
                synchronized (LOCK) { guardedCounter++; } // one thread at a time: safe
                atomicCounter.incrementAndGet();          // single atomic instruction: safe
            }
        };
        Thread[] ts = new Thread[4];
        for (int i = 0; i < 4; i++) ts[i] = new Thread(bump);
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();

        System.out.println("expected              = " + (4 * 100_000));
        System.out.println("volatile counter      = " + volatileCounter);
        System.out.println("synchronized counter  = " + guardedCounter);
        System.out.println("atomic counter        = " + atomicCounter.get());
    }

    // C. Compare-and-set: the retry loop every atomic class hides from you.
    static void casLoop() {
        System.out.println("--- C. compare-and-set ---");
        AtomicInteger max = new AtomicInteger(0);
        int[] samples = {3, 9, 4, 11, 7};
        for (int s : samples) {
            int current;
            do {
                current = max.get();
                if (s <= current) break;
            } while (!max.compareAndSet(current, s)); // retry if someone else changed it
        }
        System.out.println("running max = " + max.get());
    }

    // D. Two locks grabbed in opposite order would deadlock; tryLock backs off instead.
    static void deadlockAvoidance() throws Exception {
        System.out.println("--- D. deadlock avoidance ---");
        ReentrantLock left = new ReentrantLock();
        ReentrantLock right = new ReentrantLock();

        Runnable leftThenRight = () -> grabBoth(left, right, "left->right");
        Runnable rightThenLeft = () -> grabBoth(right, left, "right->left");

        Thread t1 = new Thread(leftThenRight, "T1");
        Thread t2 = new Thread(rightThenLeft, "T2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("both threads finished - no deadlock");
    }

    static void grabBoth(ReentrantLock first, ReentrantLock second, String order) {
        for (int attempt = 1; attempt <= 10; attempt++) {
            try {
                if (first.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        if (second.tryLock(50, TimeUnit.MILLISECONDS)) {
                            try {
                                System.out.println(Thread.currentThread().getName()
                                        + " got both locks (" + order + ") on attempt " + attempt);
                                return;
                            } finally { second.unlock(); }
                        }
                    } finally { first.unlock(); }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println(Thread.currentThread().getName() + " gave up (" + order + ")");
    }
}
