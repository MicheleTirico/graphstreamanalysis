package analyzers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

 import graphTool.GraphTool;
import handleFile.expCsv;
import statistical_and_math_tools.Frequency;

public class BcAnalysis {

	private Graph g ;
	private double maxNorm , minNorm , aveNorm;
	Map<Node , Double> 	mapNodeBc = new HashMap<Node,Double>() ,
						mapNodeBcNorm = new HashMap<Node,Double>() ;
	
	public BcAnalysis ( Graph g ) {
		this.g = g ;
	}
	
	public void compute ( String attributeWeigth ) {
		if ( attributeWeigth != null)
			for ( Edge e : g.getEachEdge())  	e.addAttribute(attributeWeigth, GraphTool.getDistGeom(e.getNode0(), e.getNode1())) ;
		
		BetweennessCentrality bcb = new BetweennessCentrality();
		if ( attributeWeigth != null) {
			bcb.setWeightAttributeName(attributeWeigth);
			
		}
	    bcb.init(g);
	    bcb.compute();
	}
	
	public void computeTopological ( String attributeName  ) {
		BetweennessCentrality bcb = new BetweennessCentrality();
	  	bcb.setCentralityAttributeName(attributeName) ;			
		bcb.init(g);
	    bcb.compute();
	}
	
	
	
	public void compute ( String attributeName , String attributeWeigth ) {
		if ( attributeWeigth != null ) 
			for ( Edge e : g.getEachEdge())  	e.addAttribute(attributeWeigth, GraphTool.getDistGeom(e.getNode0(), e.getNode1())) ;
		
		BetweennessCentrality bcb = new BetweennessCentrality();
		if ( attributeWeigth != null ) {
			bcb.setWeightAttributeName(attributeWeigth);
			bcb.setCentralityAttributeName(attributeName) ;			
		}
		
		bcb.init(g);
	    bcb.compute();
//	    bcb.registerProgressIndicator(progress);
	}
	
	public void computeNorm ( String attributeNorm ) {
		 
		 maxNorm = -10 ; minNorm = +11 ; aveNorm = 5 ;
		 for ( Node n : g.getEachNode()) {		// 	System.out.println(n.getAttributeKeySet());
			 int nc = GraphTool.getNodesOfGiant("test", g, n.getId()).size() ;
			 double norm = 2.0 / ( (nc -1) * (nc - 2 ));
	       	double cb = n.getAttribute("Cb") ;
	       	double cbNorm = cb * norm  ;
	       	if ( cbNorm < minNorm  ) minNorm = cbNorm ; 
	       	if ( cbNorm >  maxNorm  ) maxNorm = cbNorm ; 
	       	n.addAttribute(attributeNorm, cbNorm); 
	       	mapNodeBc.put(n, cb);
	       	mapNodeBcNorm.put(n, cbNorm);
		 } 
	}
	
	public void computeNorm ( String attributeOrigin, String attributeNew ) {
		
		int nc = g .getNodeCount();
		 maxNorm = -10 ; minNorm = +11 ; aveNorm = 5 ;
		 for ( Node n : g.getEachNode()) {		// 	System.out.println(n.getAttributeKeySet());
 
//			int nc =   // GraphTool.getNodesOfGiant("giant", g , n.getId()).size() ;//
			double norm = 1.0 / ( (nc -1) * (nc - 2 ));
		  	double cb = n.getAttribute(attributeOrigin) ;
		  	double cbNorm = cb * norm  ;
		 // 	System.out.println("ciao");
		//	
		  	if (cbNorm > 1 )
		  	System.out.println(n.getId() + " " + cb);
//		  	if ( cbNorm < minNorm  ) minNorm = cbNorm ; 
//		  	if ( cbNorm >  maxNorm  ) maxNorm = cbNorm ; 
	  		n.addAttribute(attributeNew, cbNorm); 
		  	mapNodeBc.put(n, cb);
		  	mapNodeBcNorm.put(n, cbNorm);
		  	
		 } 
	}
	
	public static ArrayList<Node> getNodesOfGiant ( String attributeGiant , Graph g , String idNode) {
		ArrayList<Node> nodes = new ArrayList<Node> () ;
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(g);
		cc.setCountAttribute(attributeGiant);
//		System.out.print(g.getNode(idNode).getAttributeKeySet() );
		String t = g.getNode(idNode).getAttribute(attributeGiant).toString();
		for ( Node n : g.getEachNode() ) {
			String test = n.getAttribute(attributeGiant).toString() ;
			if ( test.equals(t)) nodes.add(n);
		}
		return nodes; 
	}
	
	public void computeNorm01 ( String attributeNorm , String attributeNorm01 ) {
		 int nc = g.getNodeCount();
		 maxNorm = -10 ; minNorm = +11 ; aveNorm = 5 ;
		 for ( Node n : g.getEachNode()) {		// 	System.out.println(n.getAttributeKeySet());
        	double cbNorm = n.getAttribute(attributeNorm) ;
   
        	if ( cbNorm < minNorm  ) minNorm = cbNorm ; 
        	if ( cbNorm >  maxNorm  ) maxNorm = cbNorm ; 
//        	n.addAttribute(attributeNorm, cbNorm); 
//        	mapNodeBc.put(n, cb);
//        	mapNodeBcNorm.put(n, cbNorm);
		 }
		 if ( attributeNorm01 != null)
			 for ( Node n : g.getEachNode()) {		// 	System.out.println(n.getAttributeKeySet());
				 double cbNorm = n.getAttribute(attributeNorm) ;
//				 System.out.println(cbNorm/maxNorm  +" "+ cbNorm +" "+ maxNorm);
				 n.addAttribute(attributeNorm01, cbNorm/maxNorm);
			 }
	}
	
	public void setColor ( String attribute ) {
		for ( Node n : g.getEachNode()) 	n.addAttribute("ui.color", (double) Math.min(1.0, n.getAttribute(attribute)));		
	}
	
	private double exp ; 
	private ArrayList<double[]> distributionCum = new ArrayList<double[]>() ,
			 					distributionRel = new ArrayList<double[]>() ,
					 			distributionAss = new ArrayList<double[]>() ;
	double[] expTest = new double[3];
	
	public void computeExponent (String attribute , int numFreqCum , int numFreqRel ,  int numFreqAss){
		WeightedObservedPoints obs = new WeightedObservedPoints();     
		ArrayList<Double> vals = new ArrayList<Double>();
		for (Node n : g.getEachNode()) vals.add( (double) n .getAttribute(attribute) ); 		    			
		for ( double[] v : Frequency.getCumulativeDistr( Frequency.getListFrequencyAss(vals, numFreqCum , 0 , 1 ,  true), false ) ) 	 	{	
			double val = (Double.isInfinite(Math.log(v[1]) ) ) ? val = 0 : Math.log(v[1]+1) ;
			obs.add( v[0]     , val );	
			distributionCum.add(new double[] { 	v[0]  , val    })  ;
		}
//		exp = PolynomialCurveFitter.create(1).fit(obs.toList())[1];
		expTest[0] = PolynomialCurveFitter.create(1).fit(obs.toList())[1];
		obs = new WeightedObservedPoints();  
		for ( double[] v :  Frequency.getListFrequencyRel(vals, numFreqRel, true) ) 	 	{
			double val = (Double.isInfinite(Math.log(v[1]) ) ) ? val = 0 : Math.log(v[1]+1) ;
			obs.add( v[0]     , val );	
			distributionRel.add(new double[] { 	v[0]  , val})  ;
		}	
		
		expTest[1] = PolynomialCurveFitter.create(1).fit(obs.toList())[1];
		obs = new WeightedObservedPoints();  
		for ( double[] v :  Frequency.getListFrequencyAss(vals, numFreqAss, 0, 1, true)) 	 	{
			double val = (Double.isInfinite(Math.log(v[1]) ) ) ? val = 0 : Math.log(v[1] +1 ) ;
			obs.add( v[0]     , val   );	
			distributionAss.add(new double[] { 	v[0]  , val    })  ;
		}
		expTest[2] = PolynomialCurveFitter.create(1).fit(obs.toList())[1];
		obs = new WeightedObservedPoints();  
	}
	
	public void computeCsv (String pathStore ) throws IOException {
		
		if (new File(pathStore).exists())			new File(pathStore).delete();

		FileWriter fw = new FileWriter(pathStore , true);
 		String header = "id;x;y;Cb;CbNorm;CbNorm01" ;
		expCsv.addCsv_header( fw , header ) ;
		
		for ( Node n: g.getEachNode() ) {
			Object test = n.getAttribute("scale");
			if (test == null ) {
				
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			double  Cb = n.getAttribute("Cb") ,
					CbNorm = n.getAttribute("CbNorm"),
					CbNorm01 = n.getAttribute("CbNorm01") ;
			
			expCsv.writeLine(fw, Arrays.asList( new String [] {
					n.getId().toString() ,
					String.format("%.3f",coords[0]) ,
					String.format("%.3f",coords[1]) ,
					String.format("%.3f",Cb ),
					String.format("%.3f",CbNorm),
					String.format("%.3f",CbNorm01 ) } ) ,';' ) ;				
			}
		}
		System.out.println("finish for " + g.getNodeCount() +" nodes/ exp -> " + expTest[0] + " " + expTest[1] + " " + expTest[2]);
		fw.close();
	}
	
	public double getExp ( ) { return exp; }
	public double[] getExpTest ( ) { return expTest; }
	public ArrayList<double[]>  getDistributionCum ( ) {return distributionCum ;}
	public ArrayList<double[]>  getDistributionRel ( ) { return distributionRel;}
	public ArrayList<double[]>  getDistributionAss ( ) { return distributionAss;}
	public Graph getGraph ( ) { return g ; }
}

