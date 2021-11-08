package analyzers;

import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.AStar.DistanceCosts;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import graphTool.GraphTool;
import graphTool.ReadDGS;

import graphgenerator.CompleteGraphGenerator;
import graphgenerator.Delaunay_generator;
import graphgenerator.RandomPlanarGenerator_shuffle;
import simplifyNetwork.Simplifier;

 
public class Efficiency  {

	private double eff ;
	private Graph g ;
	ArrayList<Node[]> pairs ;   
	AStar astar ;
	
	public Efficiency ( Graph g ) {
		this.g = g ; 
		pairs = GraphTool.getPairs(g,false,false);
		astar = new AStar(g) ;
	}
	
	
	public void computeTopo () {
		 double tot = 0  ;
		 for ( Node[] pair : pairs) {
			astar.compute(pair[0].getId(),pair[1].getId()); 
			Path path = astar.getShortestPath();		 
			try { tot += 1 /path.getEdgeCount() ;
			} catch (NullPointerException e) {tot += 0 ; }
 		}
		double v = g.getNodeCount();
		eff =  tot * 2 / ( v * (v - 1 ) )    ;
	}
	
	public void computeGeom () {
		 double tot = 0  ;
		 astar.setCosts(new DistanceCosts());
		 for ( Node[] pair : pairs) {
			astar.compute(pair[0].getId(),pair[1].getId()); 
			Path path = astar.getShortestPath();
			try { tot =+  1 / GraphTool.getLengthPath(path);;
			} catch (NullPointerException e) {tot += 0 ; }
			
			tot = tot + 1 / GraphTool.getLengthPath(path);
		}
		double v = g.getNodeCount();
		eff =  tot * 2 / ( v * (v - 1 ) )    ;
	}
	
	
	public double getEffTopo () { 
		computeTopo();
		return   eff  ;
	}
	
	public double getEffGeom () { 
		computeGeom();
		return   eff  ;
	}
	
	public static void main(String[] args) throws IOException {
//		String path = "data/basic_graph_dgs/" ;
//		Graph g = new CompleteGraphGenerator("complete", 0, 20, new int[] {10,10} ,true).getGraph();
//		Graph g = new Delaunay_generator("del", 0, 100, new int[] {10,10} ,true).getGraph();
//		Graph g = new Delaunay_generator("del", 0, 100, new int[] {10,10} ,true).getGraph();
//		RandomPlanarGenerator_shuffle r = new RandomPlanarGenerator_shuffle("ra", 0, 100, new int[] {10,10}) ;
//		r.compute(.5);
//		Graph g = r.getGraph(); 
//		g = GraphTool.getGiantGraph(g, true);
//		g.display(false);

 
		String path = "data/cities_dgs/Ajaccio.dgs" ; 
		Graph 	g = new ReadDGS(path).getGraph() , 
				sim = Simplifier.getSimplifiedGraph(g, true);
		sim.display(false);
		System.out.println("info -> " + sim.getNodeCount() );
		double effTopo = new Efficiency(g).getEffTopo () ;

		System.out.println("eff topo -> " + effTopo );
		
		double effGeom = new Efficiency(g).getEffGeom () ;
		System.out.println("eff geom -> " + effGeom );
	}

}
