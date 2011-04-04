package shef.network;

import java.io.Serializable;

import org.neuroph.core.input.SummingFunction;

public class SumMax extends SummingFunction implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double getOutput(double[] arg0) {
//		System.out.println(Arrays.toString(arg0));
		double max = 0;
		double sum = 0;
		for(double d : arg0){
			sum+= d;
			if(d > max)
				max = d;
		}
		
		return max * arg0.length;
//		return sum;
	}

}
