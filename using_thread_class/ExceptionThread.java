package using_thread_class;

public class ExceptionThread {

    public static void main(String[] args) {
        Thread t = new Thread( () -> {
           throw new RuntimeException("Intentional Exception");
        });
        t.setName("Misbehaving Thread");
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("ERROR IN THREAD " + t.getName() +
                        " | ERROR " + e.getMessage());
            }
        });

        t.start();
    }
}
