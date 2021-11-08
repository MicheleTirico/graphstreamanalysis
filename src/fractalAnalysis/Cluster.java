package fractalAnalysis;

import java.util.Collection;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class Cluster {
	private Node startNode;
	private String id ;
	private Collection<Node> colNodes = new HashSet<Node> ();
//	Collection<Edge> colEdges = new HashSet<Edge> () ;
	public Cluster( String id , Node startNode ) {
		this.id = id ;
		this.startNode = startNode ;
	}

	public void putNode( Node n) {
		colNodes.add(n) ;
	}
	
	public Collection<Node> getNodes () {
		return colNodes ;
	}
	
	public double getAverageDegree () {
		double degreeTot = 0 ;
		for ( Node n : this.colNodes ) {
			degreeTot = degreeTot + n.getDegree();
		
		}
		return degreeTot / ( 2 * getEdgeCount() ) ;
	}
	
	public int getEdgeCount ( ) {
		Collection<Edge> colEdges = new HashSet<Edge> () ;
		for ( Node n : this.colNodes ) {
			colEdges.addAll(n.getEdgeSet()) ;
		}
		return colEdges.size();
	}
}
