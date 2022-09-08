package race_condition;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		Inventory inventory = new Inventory();
		IncrementingThread t1 = new IncrementingThread(inventory);
		DecrementingThread t2 = new DecrementingThread(inventory);
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		
		System.out.println("We currently have " + inventory.getItems() + " items");
	}
	
	public static class IncrementingThread extends Thread {
		private Inventory inventory;
		
		public IncrementingThread(Inventory inventory) {
			this.inventory = inventory;
		}

		@Override
		public void run() {
			for(int i = 0; i < 10000; i++) {
				inventory.increment();
			}
		}
		
		
	}
	
	public static class DecrementingThread extends Thread {
		private Inventory inventory;

		public DecrementingThread(Inventory inventory) {
			this.inventory = inventory;
		}

		@Override
		public void run() {
			for(int i = 0; i < 10000; i++) {
				inventory.decrement();
			}
		}
		
		
	}
	
	public static class Inventory {
		private Object lock = new Object();
		// Lock Free approach
//		private int items = 0;
		private AtomicInteger items = new AtomicInteger(0);
		
		public void increment() {
			items.incrementAndGet();
		}
		public void decrement() {
			items.decrementAndGet();
		}
		
		public int getItems() {
			return this.items.get();
		}
	}
}
