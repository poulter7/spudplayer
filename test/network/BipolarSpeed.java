package network;

import shef.network.Bipolar;


public class BipolarSpeed {

    private static final int limit = 100000000;
    
    public static void main(String[] args) {
        long st = System.currentTimeMillis();
        Bipolar b = new Bipolar(1);
        for(int i=0; i < limit; i++){
            b.getOutput(i);
        }
        long end = System.currentTimeMillis();
        System.out.println(end-st + " ms");
    }
}
