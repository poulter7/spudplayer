package shef.network;

import java.util.Random;

/**
 * Class to duration of {@link Bipolar} activation function class query
 * @author jonathan
 *
 */
public class BipolarStress {

	/** times to repeat test */
	private final int testNum = 50000;

	public static void main(String[] args) {
		new BipolarStress();
	}

	public BipolarStress() {
		final Bipolar b = new Bipolar(1);
		final Random r = new Random(0);
		
		long nanos = 0;
		for (int i = 0; i < testNum; i++) {
			long startTime = System.nanoTime();
			b.getOutput((r.nextFloat() * 2) - 1);
			nanos += System.nanoTime() - startTime;
		}
		System.out.println("average " + nanos / testNum + " times.");
	}

}
