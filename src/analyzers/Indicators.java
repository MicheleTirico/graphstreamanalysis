package analyzers;

import java.util.ArrayList;
import java.util.Collection;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import graphTool.BucketSet;
import graphTool.BucketSet.Bucket;
import graphTool.GraphTool;
import statistical_and_math_tools.Frequency;
 
public class Indicators extends GraphTool{

	// path length distribution ass
	public static ArrayList<double[]> getPathLengthDistributionAss ( Graph graphToAnalyze , int numberFrequency  , double valMin , double valMax ) {
		ArrayList<Double> vals = new ArrayList<Double>() ;
		for ( Edge e : graphToAnalyze.getEachEdge() ) {
			double length = e.getAttribute("length");
			vals.add(length) ;
		}
		return Frequency.getListFrequencyAss(vals, numberFrequency ,valMin , valMax, false) ; 
	}
	
	// path length distribution rel
	public static ArrayList<double[]> getPathLengthDistributionRel ( Graph graphToAnalyze , int numberFrequency , boolean isPercent , boolean isLog) {
		ArrayList<Double> vals = new ArrayList<Double>() ;
		for ( Edge e : graphToAnalyze.getEachEdge() ) {
			double len = e.getAttribute("length");
			if ( len != 0  )
				vals.add(len) ;
		}
		return Frequency.getListFrequencyRel(vals, numberFrequency , isPercent ) ; 
	}
	
	public static double getEfficiencyTopo (Graph g ) {
		double eff = 0 ; 
		ArrayList<Node[]> pairs = getPairs(g,false,false);   
		AStar astar = new AStar(g) ;
		
		 double tot = 0  ;
		 for ( Node[] pair : pairs) {
			astar.compute(pair[0].getId(),pair[1].getId()); 
			Path path = astar.getShortestPath();
			try { tot += 1 /path.getEdgeCount() ;
			} catch (NullPointerException e) {tot += 0 ; }
		}
		double v = g.getNodeCount();
		eff =  tot * 2 / ( v * (v - 1 ) )    ;
				
		return eff ;
	}
//	public static double getHeavisideStepFunction ( Graph g , double l ) {
//		double sum = 0 ;
//		for ( Node n0 : g.getEachNode() ) {			
//			for ( Node n1: g.getEachNode()) {
//				if ( ! n0.equals(n1) && graphTool.GraphTool.getDistGeom(n0, n1) < l ) {
//					sum += 1 ; 
//				}
//			}
//		}
//		return sum; 
//	}
	
	public static double getHeavisideStepFunction ( Collection<Node> nodes , double l , BucketSet bs  ) {
		double sum = 0 ;
		for ( Node n0 : nodes) {	
			double[] coords = GraphPosLengthUtils.nodePosition(n0);
			Bucket b = bs.getBucket(coords);
			Collection<Node> nodesBk = b.getNodes();
			for ( Node n1 : nodesBk ) {
				if ( ! n0.equals(n1) && GraphTool.getDistGeom(n0, n1) < l ) {
					sum += 1 ; 
				}
			}
		}
		return sum; 
	}
	
	public static double getHeavisideStepFunction ( Collection<Node> nodes , double l  ) {
		double sum = 0 ;
		for ( Node n0 : nodes) {			
			for ( Node n1 : nodes ) {
				if ( ! n0.equals(n1) && GraphTool.getDistGeom(n0, n1) < l ) {
					sum += 1 ; 
				}
			}
		}
		return sum; 
	}
	

}
