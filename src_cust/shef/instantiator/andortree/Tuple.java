package shef.instantiator.andortree;

public class Tuple <T,K>{

	private T a;
	private K b;
	public Tuple(T first, K second) {
		this.a = first;
		this.b = second;
	}
	
	public T getFirst(){
		return a;
	}
	
	public K getSecond(){
		return b;
	}
	
	public String toString(){
		return "["+a + " -> " +b+"]";
	}
}
