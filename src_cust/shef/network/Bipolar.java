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

	private static final long serialVersionUID = 1L;
	private final double slope;
	
	public Bipolar(double slope){
		this.slope = slope;
	}
	
	public double getOutput(final double net) {
		double exp = Math.exp(this.slope * -net);
		return (2 / (1 + exp)) - 1;
	}
	
}
