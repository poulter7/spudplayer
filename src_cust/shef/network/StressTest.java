package shef.network;

public class StressTest {

	protected volatile boolean cont = true;
	
	void halt(){
		cont = false;
	}
	
}
