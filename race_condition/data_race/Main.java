package race_condition.data_race;

public class Main {
	
	public static void main(String[] args) {
		SharedClass obj = new SharedClass();
		
		Thread t1 = new Thread(() -> {
			for(int i =0; i < Integer.MAX_VALUE; i++) {
				obj.increment();
			}
		});
		
		Thread t2 = new Thread(() -> {
			for(int i = 0; i < Integer.MAX_VALUE; i++) {
				obj.isDataRace();
			}
		});
		t1.start();
		t2.start();
	}
	
	public static class SharedClass {
		private volatile int x = 0;
		private volatile int y = 0;
		
		public void increment() {
			// Data race section: Because compiler and CPU can
			// reorder these statements for better performance. 
			// To resolve this issue we need to add volatile keyword
			// that will gurantee the order of execution.
			x++;
			y++;
		}
		
		public void isDataRace() {
			if(y > x) {
				System.out.println("Data Race");
			}
		}
		
	}
	
}
