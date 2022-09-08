package using_thread_class;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are now in thread " + Thread.currentThread().getName());
                System.out.println("Priority is: " + Thread.currentThread().getPriority());
            }
        });
        thread.setName("Test thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println("We are in thread " + Thread.currentThread().getName() + " before start");
        thread.start();
        System.out.println("We are in thread " + Thread.currentThread().getName() + " after start");

//        Thread.sleep(1000);
    }
}
