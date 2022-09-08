package AtomicReference;

import java.util.concurrent.atomic.AtomicReference;

public class Main {
	
	public static void main(String[] args) {
		String oldValue = "old value";
		String newValue = "New value";
		AtomicReference<String> atomicReference = new AtomicReference<String>(oldValue);
		
		if(atomicReference.compareAndSet(oldValue, newValue)) {
			System.out.println("AtomicReference becomes " + atomicReference.get());
		}
		else {
			System.out.println("New value is not set");
		}
		
	}
	
}
