package thread_join;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		List<Long> numbers = Arrays.asList(0L,3435L,3543L,2324L,46566L,23L,5556L);
		List<FactorialThread> threads = new ArrayList<>();
		
		for(long input : numbers) {
			threads.add(new FactorialThread(input));
		}
		
		for(Thread t : threads) {
			t.setDaemon(true);;
			t.start();
		}
		
		for(Thread t : threads) {
			t.join(2000);
		}
		
		for(int i =0; i < threads.size(); i++) {
			FactorialThread t = threads.get(i);
			
			if(t.isFinished) {
				System.out.println("Factorial of " + numbers.get(i)
				+ " is " + t.getResult());
			} 
			else {
				System.out.println("The calculation for " + numbers.get(i)
				+ " is still in progress");
			}
		}
		
	}
	
	private static class FactorialThread extends Thread {
		private long inputNumber;
		private BigInteger result = BigInteger.ZERO;
		private boolean isFinished = false;
		
		public FactorialThread(long inputNumber) {
			this.inputNumber = inputNumber;
		}

		@Override
		public void run() {
			this.result = this.factorial(inputNumber);
			this.isFinished = true;
		}
		
		public BigInteger factorial(long n) {
			BigInteger tempResult = BigInteger.ONE;
			
			for(long i = n; i > 0; i--) {
				tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
			}
			return tempResult;
		}
		
		public boolean isFinished() {
			return isFinished;
		}
		
		public BigInteger getResult() {
			return result;
		}
		
	}
}
