package fractalAnalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import graphTool.GraphTool;

 
public class ClusterPath {
	private Graph graph ;
	private int idClusterInt = 0 ; 
	private int [] sizeSpace ;
	private double  fd = 0 ;
	double[] minMaxIncrem ;
	private ArrayList<double[]>	distribution = new ArrayList<double[]>() ,
								distributionLog = new ArrayList<double[]>() ;
//								distrAvDeg = new ArrayList<double[]>() ,
//								distrAvDegLog = new ArrayList<double[]>() ;
	 
	public ClusterPath ( Graph graph , int[] sizeSpace) { 
		this.graph = graph ;
		this.sizeSpace = sizeSpace; 
	}

	public void setParams( double[] minMaxIncrem ) {
		this.minMaxIncrem = minMaxIncrem  ;
	}

	public void compute () {
		for ( double dist = minMaxIncrem[0] ; dist <minMaxIncrem[1] ; dist = dist + minMaxIncrem[2] ) {
			Collection<Cluster> clusters = getClustersPath(dist);
			distribution.add(new double[] { dist, clusters.size() } ) ;
			distributionLog.add(new double[] { Math.log(dist), Math.log(clusters.size() ) } ) ;
			System.out.println(dist +" " + clusters.size());
		}
	}
	public Collection<Cluster> getClustersPath ( double distMax  ) {
		Collection<Cluster> clustersPath = new HashSet<Cluster>() ;
		Collection<Node> nodeVisited = new HashSet<Node>() ;
		for (Node n : graph.getEachNode() ) {
			if ( ! nodeVisited.contains(n)) {
				double dist = 0 ;
				Node previous = n ;
				nodeVisited.add(n);
				Cluster c = new Cluster(Integer.toString(idClusterInt++), n) ;
				clustersPath.add(c);
				DepthFirstIterator<Node> dfi = new DepthFirstIterator<Node>(n);
				while ( dfi.hasNext() ) {			
					Node next = dfi.next() ;
					dist = dist + GraphTool.getDistGeom(previous,next);
					if ( dist< distMax && ! nodeVisited.contains(next) ) {
						nodeVisited.add(next);
						c.putNode(next);
						previous = next ;
					} else 
						dist= 0 ;
				}				
			}
		}
		return clustersPath ;
	}
	
	public Collection<Cluster> getClustersGeom(  double distMax ) {
		 Collection<Cluster> clustersGeom = new HashSet<Cluster>();
		Collection<Node> nodeVisited = new HashSet<Node>() ;
		for (Node n : graph.getEachNode() ) {
			if ( ! nodeVisited.contains(n)) {
				nodeVisited.add(n);
				Cluster c = new Cluster(Integer.toString(idClusterInt++), n) ;
				clustersGeom.add(c);
				DepthFirstIterator<Node> dfi = new DepthFirstIterator<Node>(n);
				while ( dfi.hasNext() ) {			
					Node next = dfi.next() ;
					double dist = GraphTool.getDistGeom(n,next);
					if ( dist< distMax && ! nodeVisited.contains(next) ) {
						nodeVisited.add(next);
						c.putNode(next);
					}
				}				
			}
		}
		return clustersGeom ;
	}
	public ArrayList<double[]> getDistribution(){
		return distribution  ;
	}
	
	public ArrayList<double[]> getLogDistribution(  ){	
		return distributionLog; 
	}
	
	public double getFractalDimension() {
		
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for ( double[] vals : distributionLog) 
			obs.add(vals[0], vals[1]);
		return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] );
		
	//	return fd; 
	}
	
// cluster average degree
//	private ArrayList<double[]>	 
//			distrAvDeg = new ArrayList<double[]>() ,
//			distrAvDegLog = new ArrayList<double[]>() ;
//	public void compute () {
//		for ( double dist = minMaxIncrem[0] ; dist <minMaxIncrem[1] ; dist = dist + minMaxIncrem[2] ) {
//			Collection<Cluster> clusters = getClustersPath(dist);
//			distribution.add(new double[] { dist, clusters.size() } ) ;
//			distributionLog.add(new double[] { Math.log(dist), Math.log(clusters.size() ) } ) ;
//			double tot = 0 ;
//			for ( Cluster c : clusters ) {
//				tot = tot + c.getAverageDegree () ;
//			}
//			
//			
//			distrAvDeg.add(new double[] { dist, tot / clusters.size() } ) ;
//			distrAvDegLog.add(new double[] {Math.log(dist) , Math.log(tot / clusters.size()) } ) ;
//			System.out.println(dist +" "  + clusters.size() ) ;
//		}
//	}

//	public ArrayList<double[]> getAverageDistribution (  ){	
//		return distrAvDeg ; 
//	}
//	
//	public ArrayList<double[]> getAverageDistributionLog (  ){	
//		return distrAvDegLog ; 
//	}
	
}
