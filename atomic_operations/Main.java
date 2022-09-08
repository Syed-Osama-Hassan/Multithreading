package atomic_operations;

import java.util.Random;

import atomic_operations.Main.BusinessLogic;

public class Main {
	
	public static void main(String[] args) {
		Metrics metrics = new Metrics();
		BusinessLogic t1 = new BusinessLogic(metrics);
		BusinessLogic t2 = new BusinessLogic(metrics);
		MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);
		
		t1.start();
		t2.start();
		metricsPrinter.start();
	}
	
	public static class MetricsPrinter extends Thread {
		private Metrics metrics;
		
		public MetricsPrinter(Metrics metrics) {
			this.metrics = metrics;
		}

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				double currentAverage = metrics.getAverage();
				System.out.println("Current average is " + currentAverage);
			}
		}
		
		
	}
	
	public static class BusinessLogic extends Thread {
		private Metrics metrics;
		private Random random = new Random();
		
		public BusinessLogic(Metrics metrics) {
			this.metrics = metrics;
		}

		@Override
		public void run() {
			while(true) {
				long startTime = System.currentTimeMillis();
				try {
					Thread.sleep(random.nextInt(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long endTime = System.currentTimeMillis();
				metrics.addSample(endTime - startTime);
			}
		}
		
		
	}
	
	public static class Metrics {
		private long count = 0;
		private volatile double average = 0.0;
		
		public synchronized void addSample(long sample) {
			double currentSum = average * count;
			count++;
			average = (currentSum + sample) / count;
		}
		
		public double getAverage() {
			return average;
		}
	}
	
}
