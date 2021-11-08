package fractalAnalysis;

import java.util.Collection;
import java.util.HashSet;

import org.graphstream.graph.Node;

public class Box {

	private String id ;
	private double [] size , minXY ;
	private Collection<Node> nodes = new HashSet<Node>();
	
	public Box (String id, double [] size , double[] minXY  ) {
		this.id = id ;
		this.size = size ;
		this.minXY= minXY;
	}
	
	public void putNode(Node n) {
		nodes.add(n);
	}
	
	public Collection<Node> getNodes() {
		return nodes; 
	}
	
	public boolean isEmpty() {
		if (nodes.isEmpty())
			return true;
		else 
			return false ;
	}
	
	public double[] getSize () {
		return size ;
	}

	public int getNumNodes () {
		return nodes.size();
	}
	
}
