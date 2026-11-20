import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

// Part 3 of the concurrency cluster: pools, futures, coordination, concurrent collections.
public class ConcurrencyDemo4 {

    public static void main(String[] args) throws Exception {
        pools();
        futures();
        failingTask();
        completableFuture();
        producerConsumer();
        latch();
        concurrentMap();
    }

    // A. One pool, many tasks: threads are reused, not re-created.
    static void pools() throws Exception {
        System.out.println("--- A. a pool reuses its threads ---");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Future<String>> results = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            final int id = i;
            results.add(pool.submit(() -> {
                Thread.sleep(20);
                return "task " + id + " ran on " + Thread.currentThread().getName();
            }));
        }
        for (Future<String> f : results) System.out.println(f.get());
        pool.shutdown();
        System.out.println("terminated cleanly = " + pool.awaitTermination(2, TimeUnit.SECONDS));
    }

    // B. Callable returns a value; Future is the receipt you redeem later.
    static void futures() throws Exception {
        System.out.println("--- B. Callable + Future ---");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Callable<Integer>> jobs = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int n = i;
            jobs.add(() -> { Thread.sleep(50L * n); return n * n; });
        }
        long start = System.currentTimeMillis();
        List<Future<Integer>> fs = pool.invokeAll(jobs);   // blocks until all are done
        int sum = 0;
        for (Future<Integer> f : fs) sum += f.get();
        long millis = System.currentTimeMillis() - start;
        System.out.println("sum of squares = " + sum);
        System.out.println("serial would take ~300ms; parallel took about " + (millis / 50 * 50) + "ms");
        pool.shutdown();
    }

    // C. A task that throws hides the exception until you call get().
    static void failingTask() {
        System.out.println("--- C. exceptions hide inside a Future ---");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<Integer> f = pool.submit(() -> 1 / 0);
        System.out.println("submitted; nothing printed yet - the failure is still asleep");
        try {
            f.get();
        } catch (ExecutionException e) {
            System.out.println("get() rethrew it: " + e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pool.shutdown();
    }

    // D. CompletableFuture: describe the pipeline, never block in the middle.
    static void completableFuture() throws Exception {
        System.out.println("--- D. CompletableFuture pipeline ---");
        CompletableFuture<String> user = CompletableFuture.supplyAsync(() -> sleepThen("user:ana", 60));
        CompletableFuture<String> orders = CompletableFuture.supplyAsync(() -> sleepThen("orders:3", 60));

        CompletableFuture<String> page = user
                .thenCombine(orders, (u, o) -> u + " | " + o)   // join two results
                .thenApply(String::toUpperCase)                 // transform
                .exceptionally(ex -> "fallback page");          // recover

        System.out.println("page = " + page.get());

        CompletableFuture<String> broken = CompletableFuture
                .<String>supplyAsync(() -> { throw new IllegalStateException("db down"); })
                .exceptionally(ex -> "fallback: " + ex.getCause().getMessage());
        System.out.println(broken.get());
    }

    static String sleepThen(String value, long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return value;
    }

    // E. BlockingQueue hands work from producers to consumers with no manual locking.
    static void producerConsumer() throws Exception {
        System.out.println("--- E. producer / consumer ---");
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);   // tiny on purpose
        String POISON = "__done__";

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put("item-" + i);                 // blocks when the queue is full
                    System.out.println("produced item-" + i + " (queue size " + queue.size() + ")");
                }
                queue.put(POISON);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = queue.take();             // blocks when the queue is empty
                    if (item.equals(POISON)) break;
                    Thread.sleep(30);
                    System.out.println("  consumed " + item);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    // F. CountDownLatch: wait until N things have happened.
    static void latch() throws Exception {
        System.out.println("--- F. CountDownLatch ---");
        int workers = 3;
        CountDownLatch ready = new CountDownLatch(workers);
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        for (int i = 1; i <= workers; i++) {
            final int id = i;
            pool.submit(() -> {
                try { Thread.sleep(30L * id); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("  worker " + id + " warmed up");
                ready.countDown();
            });
        }
        ready.await();
        System.out.println("all workers ready - starting traffic");
        pool.shutdown();
    }

    // G. ConcurrentHashMap: atomic compound operations, no locking by hand.
    static void concurrentMap() throws Exception {
        System.out.println("--- G. ConcurrentHashMap ---");
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        String[] words = {"a", "b", "a", "c", "b", "a"};
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch done = new CountDownLatch(words.length);
        for (String w : words) {
            pool.submit(() -> {
                counts.merge(w, 1, Integer::sum);   // atomic read-modify-write per key
                done.countDown();
            });
        }
        done.await();
        pool.shutdown();
        System.out.println("counts = " + new java.util.TreeMap<>(counts));

        Map<String, Integer> plain = new java.util.HashMap<>();
        plain.put("a", 1);
        System.out.println("HashMap is fine when only one thread touches it: " + plain);
    }
}
