package shef.network;

public class StopTimer implements Runnable {
	
	StressTest stopThis;
	long timeToLeave; 
	
	public StopTimer(StressTest s, long time) {
		stopThis = s;
		timeToLeave = time;
	}

	@Override
	public void run() {
		System.out.println("starting");
		try {
			Thread.sleep(timeToLeave);
			stopThis.halt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
