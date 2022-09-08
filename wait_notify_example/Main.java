package wait_notify_example;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class Main {
	private static final int N = 10;
	private static final String INPUT_FILE = "./resources/matrices";
	private static final String OUTPUT_FILE= "./resources/matrices_result.txt";
	
	public static void main(String[] args) throws IOException {
		ThreadSafeQueue queue = new ThreadSafeQueue();
		File inputFile = new File(INPUT_FILE);
		File outputFile = new File(OUTPUT_FILE);
		
		MatricesReaderProducer producer = new MatricesReaderProducer(
				new FileReader(inputFile), queue);
		MatricesMultiplierConsumer consumer = new MatricesMultiplierConsumer(
				queue, new FileWriter(outputFile));
		
		producer.start();
		consumer.start();
	}
	
	public static class MatricesMultiplierConsumer extends Thread {
		private ThreadSafeQueue queue;
		private FileWriter fileWriter;

		public MatricesMultiplierConsumer(ThreadSafeQueue queue, FileWriter fileWriter) {
			this.queue = queue;
			this.fileWriter = fileWriter;
		}
		
		private static void saveMatrixToFile(FileWriter fileWriter, float [][] matrix) throws IOException {
			for(int r = 0; r < N; r++) {
				StringJoiner joiner = new StringJoiner(",");
				for(int c = 0; c < N; c++) {
					joiner.add(String.format("%.2f", matrix[r][c]));
				}
				fileWriter.write(joiner.toString());
				fileWriter.append("\n");
			}
			fileWriter.append("\n");
		}
		
		@Override
		public void run() {
			while(true) {
				MatrixPair matrixPair = queue.remove();
				if(matrixPair == null) {
					System.out.println("No more matrices to read from the queue. Consumer is terminating");
					break;
				}
				
				float [][] result = multiplyMatrices(matrixPair.matrix1, matrixPair.matrix2);
				
				try {
					saveMatrixToFile(fileWriter, result);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public float[][] multiplyMatrices(float[][] m1, float [][] m2) {
			float [][] result = new float[N][N];
			for(int r =0; r < N; r++) {
				for(int c = 0; c < N; c++) {
					for(int k = 0; k < N; k++) {
						result[r][c] += m1[r][k] + m2[k][c];
					}
				}
			}
			
			return result;
		}
	}
	
	public static class MatricesReaderProducer extends Thread {
		private Scanner scanner;
		private ThreadSafeQueue queue;
		
		public MatricesReaderProducer(FileReader fileReader, ThreadSafeQueue queue) {
			this.scanner = new Scanner(fileReader);
			this.queue = queue;
		}
		
		
		
		@Override
		public void run() {
			while(true) {
				float [][] matrix1 = readMatrix();
				float [][] matrix2 = readMatrix();
				
				if(matrix1  == null || matrix2 == null) {
					queue.terminate();
					System.out.println("No more matrices to read. Producer thread is terminating");
					return;
				}
				
				MatrixPair matrixPair = new MatrixPair();
				matrixPair.matrix1 = matrix1;
				matrixPair.matrix2 = matrix2;
				queue.add(matrixPair);
			}
			
		}



		private float [][] readMatrix() {
			float [][] matrix = new float[N][N];
			for(int row = 0; row < N; row++) {
				if(!scanner.hasNext()) {
					return null;
				}
				String [] line = scanner.nextLine().split(",");
				for(int c = 0; c < N; c++) {
					matrix[row][c] = Float.valueOf(line[c]);
				}
			}
			scanner.nextLine();
			return matrix;
		}
		
	}
	
	public static class ThreadSafeQueue {
		private Queue<MatrixPair> queue = new LinkedList<>();
		private boolean isEmpty = true;
		private boolean isTerminate = false;
		private final int CAPACITY = 10;
		
		public synchronized void add(MatrixPair matrixPair) {
			while(queue.size() == CAPACITY) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			queue.add(matrixPair);
			isEmpty = false;
			notify();
		}
		
		public synchronized MatrixPair remove() {
			MatrixPair matrixPair = null;
			while(isEmpty && !isTerminate) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(queue.size() == 1) {
				isEmpty = true;
			}
			
			if(queue.size() == 0 && isTerminate) {
				return null;
			}
			
			System.out.println("Queue Size: " + queue.size());
			matrixPair = queue.remove();
			
			if(queue.size() == CAPACITY - 1) {
				notifyAll();
			}
			
			return matrixPair;
		}
		
		public synchronized void terminate() {
			isTerminate = true;
			notifyAll();
		}
	}
	public static class MatrixPair {
		private float [][] matrix1;
		private float [][] matrix2;
	}
}
