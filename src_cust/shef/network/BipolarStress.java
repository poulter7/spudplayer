package shef.network;

import java.util.Collections;
import java.util.Random;

public class BipolarStress extends StressTest {

	private final long stressTime = 1000;

	public static void main(String[] args) {
		new BipolarStress();
	}

	public BipolarStress() {
		int eval = 0;
		Bipolar b = new Bipolar(1);

		new Thread(new StopTimer(this, stressTime)).start();
		Random r = new Random(0);
		int testNum = 50000;
		long nanos = 0;
		for (int i = 0; i < testNum; i++) {
			long startTime = System.nanoTime();
			b.getOutput((r.nextFloat() * 2) - 1);
			nanos += System.nanoTime() - startTime;
		}
		System.out.println("average " + nanos / testNum + " times.");
	}

}
