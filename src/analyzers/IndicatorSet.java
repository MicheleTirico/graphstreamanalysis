package analyzers;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import fractalAnalysis.BoxCounting;
import analyzers.*;

public class IndicatorSet   {

	private Graph graphToAnalyze;
	private String  path  ;
	private FileWriter fwGI, fwAD , fwSC, fwSCInt, fwNDD , fwPLD , fwDD , fwEC, fwTEL , fwTLMST;
	private boolean isSim ;

	public enum indicatorArr {
		degreeDistribution
	}
	public enum indicator {
		seedCountAttr ("seedCount", false ) ,
		totalEdgeLengthAttr ( "totalEdgeLengthAttr", false),
		seedCount("seedCount" , false ),
		seedCountInt("seedCount", false ) ,
		gammaIndex("gammaIndex", false ) ,
		averageDegree("averageDegree", false ) ,
		degreeDistribution ("degreeDistribution", true , 0,0,0) ,
		normalDegreeDistribution ("normalDegreeDistribution", true , 0 , 0 , 0 ),
		pathLengthDistribution("pathLengthDistribution", true , 0 , 0 , 0 ) ,
		totalEdgeLength("totalEdgeLength", false ) ,
		totalEdgeLengthCeck("totalEdgeLength" , false ) ,
		edgeCount ("edgeCount" , false ) ,
		nodeCount ("nodeCount", false) ,
		totalEdgeLengthMST ("totalLengthEdgeMST" , false ) ,
		stepMax ("stepMax", false ) ,
		fractalDimensionBox("fractalDimensionBox", false ) ,
		organicRatio("organicRatio", false),
		meshedness("meshedness",false) ,
		averageLen ("averageLen", false),
		cost ("cost" , false) ,
		efficiencyTopo ("efficiencyTopo" , false ) ,
		efficiencyGeom ("efficiencyGeom" , false ) ,
		robustness ("robustness", false )
		;

		private String id ;
		private boolean isList ;
		private int numFreq;
		private double minVal, maxVal ;
		private String header ;

		private indicator ( String id , boolean  isList ) {
			this.id = id ;
			this.isList = isList ;
		}

		private indicator ( String id , boolean  isList, int numFreq, double minVal, double maxVal ) {
			this.id = id ;
			this.isList = isList ;
		}

		public void setFrequencyParameters ( int numFreq, double minVal, double maxVal ) {
			this.numFreq = numFreq;
			this.minVal = minVal;
			this.maxVal = maxVal ;
		}

		public double[] getFrequencyParameters () {
			return new double[] {numFreq,minVal,maxVal};
		}

		public void setHeader ( ) {
			if ( isList) {		// System.out.println(maxVal + " " + minVal + " " + numFreq);
				double gap = ( maxVal - minVal ) / numFreq ;
				header = getId() + ";";
				int x = 1 ;
				while ( x < numFreq ) {
					NumberFormat nf = NumberFormat.getNumberInstance();
					nf.setMaximumFractionDigits(1);
					String rounded = nf.format(gap * x  );
					header = header + rounded + ";";
					x++ ;
				}
			}
			else
				header = getId();
		}
		public String getHeader ( ) {
			return header;
		}
		public String getId ( ) {
			return id ;
		}

		public void setId ( String id ) {
			this.id = id ;
		}
		public void setIsList ( boolean isList ) {
			this.isList = isList ;
		}
		public boolean getIsList() {
			return isList ;
		}

	}

	public void setIsSim ( boolean isSim ) {
		this.isSim = isSim ;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public void setFw (indicator in) throws IOException {
		// System.out.println(path);
		switch (in) {
			case gammaIndex:
				fwGI = new FileWriter(path , true);
				break;
			case averageDegree :
				fwAD = new FileWriter(path , true);
				break ;
			case normalDegreeDistribution :
				fwNDD = new FileWriter(path, true);
				break ;
			case pathLengthDistribution :
				fwPLD = new FileWriter(path,true) ;
				break;
			case degreeDistribution :
				fwDD = new FileWriter(path, true) ;
				break ;
			case totalEdgeLength :
				fwTEL = new FileWriter(path,true) ;
				break ;
			case edgeCount :
				fwEC = new FileWriter(path,true) ;
				break ;
			case totalEdgeLengthMST :
				fwTLMST = new FileWriter(path,true) ;
				break ;
		}
	}

	public FileWriter getFw (indicator in ) {
		FileWriter fw = null ;
		switch (in) {
			case gammaIndex:
				fw = fwGI;
				break;
			case seedCount :
				fw = fwSC;
				break ;
			case seedCountInt :
				fw = fwSCInt ;
				break ;
			case averageDegree :
				fw = fwAD;
				break ;
			case normalDegreeDistribution :
				fw = fwNDD ;
				break ;
			case pathLengthDistribution :
				fw = fwPLD;
				break;
			case degreeDistribution :
				fw = fwDD ;
				break ;
			case totalEdgeLength :
				fw = fwTEL ;
				break ;
			case edgeCount :
				fw = fwEC ;
				break ;
			case totalEdgeLengthMST :
				fw = fwTLMST ;
				break ;
		}
		return fw ;
	}

	public void setGraph ( Graph graph ) {
		this.graphToAnalyze = graph;
	}

// GET VALUE ----------------------------------------------------------------------------------------------------------------------------------------
	public double[] getValueArr ( indicator in ) {
		double[] val = null ;
		double[] freqParams = in.getFrequencyParameters();

		switch (in) {
			case normalDegreeDistribution:
				val = getNDD ((int) freqParams[0] );
				break;
			case pathLengthDistribution:
				val = getPLD((int) freqParams[0] ,freqParams[1] ,freqParams[2] );
				break ;
			case degreeDistribution :
				val = getDD ((int)freqParams[0] ) ;
				break ;
		} //	System.out.println(graphToAnalyze + " "+ in.getId() + " "+ val);
		return val ;
	}

	public double getValue ( indicator in ) {
		double val = 0 ;
		try {
			switch (in) {
			case cost :
				val =getTotalEdgeLengthMST ( isSim )   / getTotalEdgeLength ( isSim ) ;
				break ;
			case efficiencyTopo :
//				val = Indicators.getEfficiencyTopo (graphToAnalyze) ;
				val = new Efficiency(graphToAnalyze).getEffTopo();
//				System.out.println(val);
				break;
			case efficiencyGeom:
				val = new Efficiency(graphToAnalyze).getEffGeom();
				break;
			case averageLen :
					val = getTotalEdgeLength()/ graphToAnalyze.getEdgeCount() * 10000;
					break ;
				case totalEdgeLengthCeck :
					val = getTotalEdgeLengthCeck();
					break ;
				case seedCountAttr :
			 		val = (int) graphToAnalyze.getAttribute("numberOfSeeds") ;
					break ;
				case stepMax :
					val = graphToAnalyze.getStep();
					break ;
				case nodeCount :
					val = graphToAnalyze.getNodeCount();
					break ;
				case gammaIndex:
					val = getGammaIndex(true);
					break;
				case averageDegree :
					val = getAverageDegree();
					break ;
				case edgeCount :
					val = getEdgeCount (isSim ) ;
					break ;
				case totalEdgeLength :
					val = getTotalEdgeLength ( isSim ) * 10  ;
					break ;
				case totalEdgeLengthMST :
					val = getTotalEdgeLengthMST ( isSim ) * 10  ;
					break ;
				case fractalDimensionBox :
 					val = getFractalDimensionBox () ;
					break ;
				case organicRatio :
					val = getOrganicRatio ();
					break ;
				case meshedness :
					val = getMeshedness();
					break;
				case robustness :
					val  = 0 ;
					break ;

			}
			} catch (NullPointerException e) {
				val = - 100000 ;
		} //	System.out.println(graphToAnalyze + " "+ in.getId() + " "+ val);
		return val ;
	}

// COMPUTE INDICATOR --------------------------------------------------------------------------------------------------------------------------------
	private double getOrganicRatio () {
		int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze) ;
		int noD2 = 0 ;
		for ( int d : degreeDistribution )
			noD2 += d ;
//		System.out.println((degreeDistribution[1] + degreeDistribution[3] ) / (double) noD2);
		return (degreeDistribution[1] + degreeDistribution[3] ) / (double) noD2 ;
	}

	private double getMeshedness() {
		return ( graphToAnalyze.getEdgeCount() - graphToAnalyze.getNodeCount() +1 ) / ( 2.0 * graphToAnalyze.getEdgeCount() - 5 );
	}

	private double getFractalDimensionBox () {
		BoxCounting bc = new BoxCounting(  graphToAnalyze , new int [] {512 ,512 } ) ;
		bc.setParamsSizeBox(new double []  { .5  ,  25 } , .5 );
		bc.computeSizeBox();
		return   bc.getFractalDimension() ;
	}

	private double getTotalEdgeLengthCeck ( ) {
		String attr = "length";
		double val = 0 ;
		for ( Edge e : graphToAnalyze.getEachEdge()) {
			double length = 0 ;
			try {
				length = e.getAttribute(attr);
			} catch (NullPointerException ex) {
//				ex.printStackTrace();
				length = getDistGeom(e.getNode0(), e.getNode1() );
			}
			val += length;
		}
		return val ;
	}

	private double getTotalEdgeLength() {
		String attr = "length";
		double val = 0 ;
		for ( Edge e : graphToAnalyze.getEachEdge()) {
			double length =  e.getAttribute(attr);
			val += length;
		}
		return val ;
	}


	// average degree
	private double getAverageDegree ( ) {
		return Toolkit.averageDegree(graphToAnalyze) ;
	}

	// gamma index
	private double getGammaIndex ( boolean isPlanar ) {
		double  n = graphToAnalyze.getNodeCount() ,
				e = graphToAnalyze.getEdgeCount() ,
				eMax = 0 ;
		if ( isPlanar )
			eMax = 3 * n - 6 ;
		else
			eMax = ( n - 1 ) * n / 2 ;

		if ( eMax == 0 || e == 0)
			return 0 ;
		else
			return e / eMax ;
	}

	// degree distribution
	private double[] getDD (  int numberFrequency  ) {
		double [] vals = new double[numberFrequency] ;
		int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze);
		for (int i = 0 ; i < degreeDistribution.length ; i++ )
			try {
				vals[i] = degreeDistribution[i];
			} catch (ArrayIndexOutOfBoundsException e) {
		}
		return vals ;
	}

	// normal degree distribution
	private double[] getNDD (  int numberFrequency  ) {
		double [] vals = new double[numberFrequency] ;
		int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze);
		double nodeCount = graphToAnalyze.getNodeCount() ;
		for ( int i = 0 ; i < degreeDistribution.length ; i++ )
			try {
				vals[i] = degreeDistribution[i] / nodeCount ;
			} catch (ArrayIndexOutOfBoundsException e) {
				break ;
			}
		return vals ;
	}

	// path length distribution
	private double[] getPLD (  int numberFrequency , double valMin , double valMax ) {
		double [] vals = new double[numberFrequency] ;
		Map<Edge, Set<Double>> mapEdgeLen = getMapEdgeValue(graphToAnalyze, "listLen");
		ArrayList<Double> listLen = new ArrayList<Double>() ;
		for ( Edge e : mapEdgeLen.keySet())
			for ( double val : mapEdgeLen.get(e))
				listLen.add(val);

		Map<Double, Double> map = new TreeMap<>(getMapFrequencyAss(listLen, numberFrequency ,valMin , valMax)) ;
		int pos = 0 ;
		for ( double key : map.keySet()) {
			vals[pos] = map.get(key);
			pos++;
		}
		return vals;
	}

	/** get edge count */
	private double getEdgeCount ( boolean isSim  ) {
		return getEdgeCountAndLength(indicator.edgeCount ,  isSim );
	}

	/** get total edge length  */
	private double getTotalEdgeLength ( boolean isSim ) {
		double val = 0 ;
		for ( Edge e : graphToAnalyze.getEachEdge()) {
			val += getDistGeom(e.getNode0(), e.getNode1()) ;
		}
		return val  ;
//		return getEdgeCountAndLength(indicator.totalEdgeLength , isSim ) ;
	}

	/** get total edge length of minimum spanning tree  */
	private double getTotalEdgeLengthMST ( boolean isSim  ) {
		// compute Krustal algoritm
		Kruskal kruskal = new Kruskal( "tree" , true , false ) ;
		kruskal.init(graphToAnalyze) ;
		kruskal.compute();
		double len = 0 ;
		for ( Edge e : graphToAnalyze.getEachEdge()) {
			boolean tree = e.getAttribute("tree");
			if ( tree )
				if ( isSim ) {
					ArrayList<Double> list = e.getAttribute("listLen");
					len = len + list.stream().mapToDouble(f -> f).sum() ;
				}
				else
				//	len = len + (double) e.getAttribute("length");
					len += getDistGeom(e.getNode0(),e.getNode1());
		}
		return len ;
	}

	/** compute both edge count and total edge length **/
	private double getEdgeCountAndLength ( indicator in , boolean isSim  ) {
		double edgeCount = 0 ;
		double totLen = 0 ;
		if ( isSim ) {
			for ( Edge e : graphToAnalyze.getEachEdge() ) {
				try {
				ArrayList<Double> listLen = e.getAttribute("listLen");
				edgeCount = edgeCount + listLen.size();
				totLen = totLen + listLen.stream().mapToDouble(a -> a).sum();
				} catch (NullPointerException ex) {
					edgeCount = edgeCount + 1 ;
					totLen = totLen + getDistGeom(e.getNode0(), e.getNode1());
				}
			}
		}
		else {
			edgeCount = graphToAnalyze.getEdgeCount();
			for ( Edge e : graphToAnalyze.getEachEdge() )
				totLen = totLen + getDistGeom(e.getNode0(), e.getNode1()) ;
		}
		if ( in.equals(indicator.edgeCount))
			return edgeCount ;
		else if ( in.equals(indicator.totalEdgeLength))
			return totLen ;
		else {
			System.out.println("not good indicator");
			return 0 ;
		}
	}

// GET VALUES FOR EACH GRAPH ELEMENT ----------------------------------------------------------------------------------------------------------------
	private Map getMapNodeValue ( Graph gr , String attr ){
		Map map = new HashMap();
		for ( Node n : gr.getEachNode() ) {
			map.put(n, n.getAttribute(attr));
		}
		return map;
	}

	private Map getMapEdgeValue ( Graph gr , String attr ){
		Map map = new HashMap();
		for ( Edge e : gr.getEachEdge() ) {
			map.put(e, e.getAttribute(attr));
		}
		return map;
	}

	public static indicator[] getAllIndicators ( ) {
		return indicator.values();
	}

// FREQUENCY DISTRIBUTION ---------------------------------------------------------------------------------------------------------------------------
	private Map<Double, Double> getMapFrequencyRel ( ArrayList<Double> listVal, int numberFrequency ) {
		Map<Double, Double> map = new HashMap<>();
		double  maxVal = listVal.stream().mapToDouble(valstat -> valstat).max().getAsDouble(),
				minVal = listVal.stream().mapToDouble(valstat -> valstat).min().getAsDouble(),
				gap = maxVal - minVal,
				increm = minVal + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double  key = minVal + gap * x / numberFrequency ,
					minFreq = minVal + x* increm ,
					maxFreq = minVal + (x + 1)* increm ,
					freq = 	listVal.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();
			map.put(  key  ,  freq );
		}
		return map ;
	}

	private static Map<Double, Double>  getMapFrequencyAss (ArrayList<Double> listVal , int numberFrequency , double valMin , double valMax  ) {
		Map<Double, Double> mapFrequency = new HashMap<Double, Double>();
		double gap = valMax - valMin ,
				increm = valMin + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double  key = valMin + gap * x / numberFrequency,
					minFreq = valMin + x* increm ,
					maxFreq = valMin + (x + 1)* increm ,
					freq = 	listVal.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();
			mapFrequency.put( key  ,  freq );
		}
		return mapFrequency;
	}

	public static Map<Double, Double> getMapFrequencyRel ( Graph graph , String attribute , int numberFrequency ) {

		Map<Double, Double> mapFrequency = new HashMap<>();
		new HashMap<>();
		ArrayList<Double> listAtr = new ArrayList<>();
		double maxAtr = listAtr.stream().mapToDouble(valstat -> valstat).max().getAsDouble();
		double minAtr = listAtr.stream().mapToDouble(valstat -> valstat).min().getAsDouble(); 		//			System.out.println("maxAtr " + maxAtr);		System.out.println("minAtr " + minAtr);

		double gap = maxAtr - minAtr;
		double increm = minAtr + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);

		for ( int x = 0 ; x < numberFrequency ; x++) {
			double key = minAtr + gap * x / numberFrequency ;

			double minFreq = minAtr + x* increm ;
			double maxFreq = minAtr + (x + 1)* increm ;

			double freq = 	listAtr.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();

			mapFrequency.put(  key  ,  freq );
		}
		return mapFrequency;
	}

	public static Map getMapFrequencyAss ( Graph graph , String attribute , int numberFrequency ) {

		Map<Double, Double> mapFrequency = new HashMap<>();
		new HashMap<>();
		ArrayList<Double> listAtr = new ArrayList<>();
																							//	System.out.println("listAtr " + listAtr);
		double maxAtr = 1;
		double minAtr = 0;

		double gap = maxAtr - minAtr;
		double increm = minAtr + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);

		for ( int x = 0 ; x < numberFrequency ; x++) {
			double key = minAtr + gap * x / numberFrequency ;
			double minFreq = minAtr + x* increm ;
			double maxFreq = minAtr + (x + 1)* increm ;
			double freq = 	listAtr.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();

			mapFrequency.put( key  ,  freq );
		}
		return mapFrequency;
	}


// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	// get spatial distance from 2 nodes
	public static double getDistGeom ( Node n1 , Node n2 ) {
		double [] 	coordN1 = GraphPosLengthUtils.nodePosition(n1) ,
					coordN2 = GraphPosLengthUtils.nodePosition(n2);
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}

	public static double getDistGeom ( double [] coordN1 , double [] coordN2 ) {
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}


}
