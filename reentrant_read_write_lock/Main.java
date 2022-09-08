package reentrant_read_write_lock;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
	public static final int HIGHEST_PRICE = 1000;
	
	public static void main(String[] args) throws InterruptedException {
		InventoryDatabase database = new InventoryDatabase();
		Random random = new Random();
		
		for(int i = 0; i < 10000; i++) {
			database.addItem(random.nextInt(HIGHEST_PRICE));
		}
		
		Thread writer = new Thread( () -> {
			while(true) {
				database.addItem(random.nextInt(HIGHEST_PRICE));
				database.removeItem(random.nextInt(HIGHEST_PRICE));
				
				try {
					Thread.sleep(10);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		writer.setDaemon(true);
		writer.start();
		
		int numberOfThreads = 7;
		List<Thread> readers = new ArrayList<>();
		
		for(int i = 0; i < numberOfThreads; i++) {
			Thread reader = new Thread( () -> {
				for(int y = 0; y < 10000; y++) {
					int upperBound = random.nextInt(HIGHEST_PRICE);
					int lowerBound = upperBound > 0? random.nextInt(upperBound) : 0;
					database.getNumberOfItemsPriceRange(lowerBound, upperBound);
				}
			});
			reader.setDaemon(true);
			readers.add(reader);
		}
		
		long startTime = System.currentTimeMillis();
		
		for(Thread t : readers) {
			t.start();
		}
		
		for(Thread t : readers) {
			t.join();
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Reading took: %d ms",endTime - startTime));
		
		
	}
	
	public static class InventoryDatabase {
		private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
		private ReentrantLock lock = new ReentrantLock();
		private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private Lock readLock = readWriteLock.readLock();
		private Lock writeLock = readWriteLock.writeLock();
		
		public int getNumberOfItemsPriceRange(int lowerBound, int upperBound) {
			readLock.lock();
			try {
				Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
				Integer toKey = priceToCountMap.floorKey(upperBound);
				
				if(fromKey == null || toKey == null) {
					return 0;
				}
				
				NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
				int sum = 0;
				
				for(int numberOfItemsForPrice : rangeOfPrices.values()) {
					sum += numberOfItemsForPrice;
				}
				
				return sum;
			}
			finally {
				readLock.unlock();
			}
		}
		
		public void addItem(int price) {
			writeLock.lock();
			try {
				Integer numberOfItemsForPrice = priceToCountMap.get(price);
				if(numberOfItemsForPrice == null) {
					priceToCountMap.put(price, 1);
				}
				else {
					priceToCountMap.put(price, numberOfItemsForPrice + 1);
				}
			}
			finally {
				writeLock.unlock();
			}
		}
		
		public void removeItem(int price) {
			writeLock.lock();
			try {
				Integer numberOfItemsForPrice = priceToCountMap.get(price);
				if(numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
					priceToCountMap.remove(price);
				}	
				else {
					priceToCountMap.put(price, numberOfItemsForPrice - 1);
				}
			}
			finally {
				writeLock.unlock();
			}
		}
		
	}
	
}
