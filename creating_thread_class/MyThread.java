package creating_thread_class;

public class MyThread extends Thread {

    @Override
    public void run() {
        this.setName("Test");
        System.out.println("Inside " + this.getName() + " thread");
    }
}
