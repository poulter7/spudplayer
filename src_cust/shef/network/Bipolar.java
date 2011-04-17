package shef.network;

import java.util.HashMap;

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
	private final double slope;
	HashMap<Double, Double> vals = new HashMap<Double, Double>(5000);
	
	public Bipolar(double slope){
		this.slope = slope;
	}
	
	public double getOutput(final double net) {
		Double d = vals.get(net);
		if(d == null){
			double val = (2d / (1d + Math.exp(this.slope * -net))) - 1d;
			vals.put(net, val);
			return val;
		} else {
			return d;
		}
		
		// return (2d / (1d + Math.exp(this.slope * -net))) - 1d 
		
		// annoyingly cannot use this approximation for EXP
		/*
		final long tmp = (long) (1512775 * (this.slope * - net) + 1072632447);
		final double exp =  Double.longBitsToDouble(tmp << 32);
		return (2d / (1d + exp)) - 1d;
		*/
	}
	
}
