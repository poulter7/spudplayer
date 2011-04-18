package shef.network;

import org.neuroph.core.transfer.TransferFunction;

/**
 * A transfer function adhering to the behaviour of
 * a Bipolar Sigmoid:
 * 
 * o(t) = 2/(1+e^(-&#946;t)) - 1
 * 
 * @author jonathan
 *
 */
public class Bipolar extends TransferFunction {

	private transient static final long serialVersionUID = 1L;
	
	/** gradient of the sigmoid */
	private final double slope;
	
	/**
	 * Create a sigmoid with a certain slope
	 * @param slope the slope the sigmoid should have
	 */
	public Bipolar(double slope){
		this.slope = slope;
	}
	
	public double getOutput(final double net) {
		return (2d / (1d + Math.exp(this.slope * -net))) - 1d;		
	}
	
}
