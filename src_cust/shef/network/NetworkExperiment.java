package shef.network;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.BasicLayer;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.util.NeuronProperties;

/**
 * A test class to demonstrate some Network timings
 * @author jonathan
 *
 */
public class NetworkExperiment {
	public static boolean stop = false;

	public static void main(String[] args) {

		// ENCOG
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(2));
		network.addLayer(new BasicLayer(2));
		network.addLayer(new BasicLayer(1));
		network.getStructure().finalizeStructure();
		network.reset();

		int eval = 0;
		double[] a = new double[] { 2, 2 };
		NeuralData d = new BasicNeuralData(a);
		d.setData(a);
		NeuralOutputHolder o = new NeuralOutputHolder();
		System.out.println("starting");
		new Thread(new StopTimer()).start();
		while (!stop) {
			network.compute(d, o);
			eval++;
		}
		System.out.println("ran Encog evaluation " + eval + " times.");

		// Neuroph
		eval = 0;
		stop = false;
		final NeuralNetwork neuNetwork = new NeuralNetwork();
		Layer l1 = new Layer(2, new NeuronProperties());
		Layer l2 = new Layer(2, new NeuronProperties());
		Layer l3 = new Layer(1, new NeuronProperties());
		for (Neuron n : l1.getNeurons()) {
			for (Neuron c : l2.getNeurons()) {
				c.addInputConnection(n);
			}
		}
		for (Neuron n : l2.getNeurons()) {
			for (Neuron c : l3.getNeurons()) {
				c.addInputConnection(n);
			}
		}
		neuNetwork.addLayer(l3);
		neuNetwork.addLayer(l2);
		neuNetwork.addLayer(l1);
		
		neuNetwork.setInputNeurons(l1.getNeurons());
		neuNetwork.setOutputNeurons(l3.getNeurons());
		
		System.out.println("starting");
		new Thread(new StopTimer()).start();
		while (!stop) {
			neuNetwork.setInput(a);
			neuNetwork.calculate();
			neuNetwork.reset();
			eval++;
		}
		System.out.println("ran Neuroph evaluation " + eval + " times.");
		
	}

	static class StopTimer implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				NetworkExperiment.stop = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
