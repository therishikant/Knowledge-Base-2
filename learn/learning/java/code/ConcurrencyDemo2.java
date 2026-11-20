// Part 1 of the concurrency cluster: what a thread is, and how it behaves.
public class ConcurrencyDemo2 {

    static void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

    public static void main(String[] args) throws Exception {
        log("--- A. who am I? ---");
        log("main is running");

        log("--- B. run() vs start() ---");
        Runnable job = () -> log("doing the work");
        Thread t1 = new Thread(job, "worker-1");
        System.out.println("state before start = " + t1.getState());
        t1.run();    // plain method call: NO new thread, runs on main
        t1.start();  // real new thread
        t1.join();   // wait for worker-1 to finish
        System.out.println("state after join   = " + t1.getState());

        log("--- C. two threads interleaving ---");
        Thread a = new Thread(() -> { for (int i = 1; i <= 3; i++) log("tick " + i); }, "A");
        Thread b = new Thread(() -> { for (int i = 1; i <= 3; i++) log("tock " + i); }, "B");
        a.start();
        b.start();
        a.join();
        b.join();
        log("both finished");

        log("--- D. sleeping and interrupting ---");
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(5000);
                log("woke up normally after 5s");
            } catch (InterruptedException e) {
                log("interrupted -> stopping early (flag now = " + Thread.currentThread().isInterrupted() + ")");
            }
        }, "sleeper");
        sleeper.start();
        Thread.sleep(100);
        System.out.println("sleeper state while sleeping = " + sleeper.getState());
        sleeper.interrupt();
        sleeper.join();
        System.out.println("sleeper state after join     = " + sleeper.getState());

        log("--- E. daemon thread ---");
        Thread d = new Thread(() -> {
            while (true) {
                try { Thread.sleep(200); } catch (InterruptedException e) { return; }
            }
        }, "background-ticker");
        d.setDaemon(true);   // JVM will not wait for this one
        d.start();
        log("daemon started, isDaemon=" + d.isDaemon());
        log("main ends - JVM exits anyway because only daemons are left");
    }
}
