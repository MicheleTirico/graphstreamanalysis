package fractalAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import handleFile.expCsv; 

public class InformationFractal {
	private SimpleRegression sr = new SimpleRegression() ;
	private Graph graph ;
	private int [] sizeSpace ;
	private double [] sizeBox = new double[2] ;
	
	private ArrayList<double[]> distribution = new ArrayList<double[]>() ,
								distributionLog = new ArrayList<double[]>() ;

	private double[] extSizeBox ;
	private double incSizeBox ; 	
//	private  Collection<Box> colBox = new HashSet<Box> () ;

	
	public InformationFractal ( Graph graph , int[] sizeSpace) { 
		this.graph = graph ;
		this.sizeSpace = sizeSpace; 
	}
	
	public void setParamsSizeBox(double[] extSizeBox ,double inc ) {
		this.extSizeBox = extSizeBox ;
		this.incSizeBox = inc ;
	}

	public void compute ( ) {
 		for ( double sizeBox = extSizeBox[0] ; sizeBox <= extSizeBox[1] ; sizeBox = sizeBox + incSizeBox ) {
 			double nc = graph.getNodeCount();
 			Collection<Double> vals = getCollValues(sizeBox);
 			double e = 0  ;
 			for ( double v : vals ) e += v/nc * Math.log(v/nc);
 			
//			System.out.print( String.format("%.1f",sizeBox) + " " +  String.format("%.2f",e) +" / ");
			distribution.add(new double[] { sizeBox, e } )  ;
			distributionLog.add(new double[] {	  Math.log(sizeBox) ,   Math.abs(e) })  ;
			sr.addData(	Math.log(sizeBox) , Math.abs(e) );
		}
	}


	public void compute ( int stopCost) {

		double v1 = 0;		
		int stab = 0 ;
		double sizeBox = extSizeBox[0] ;
		while ( sizeBox < extSizeBox[1] && stab < stopCost ) {
  			double nc = graph.getNodeCount();
 			Collection<Double> vals = getCollValues(sizeBox);
  			double v1Test = 0 ;
 			for ( double v : vals ) v1Test += v/nc * Math.log(v/nc);
 			if ( v1Test == v1  ) stab ++ ;
			else {
				v1 = v1Test ;
				stab = 0 ; 
			}
		
 			
//			System.out.println( stab +" " + String.format("%.1f",sizeBox) + " " +  String.format("%.2f",v1) +" / ");
			distribution.add(new double[] { sizeBox, v1 } )  ;
			distributionLog.add(new double[] {	  Math.log(sizeBox) ,   Math.abs(v1) })  ;
			sr.addData(	Math.log(sizeBox) , Math.abs(v1) );
			sizeBox = sizeBox + incSizeBox ;			
		}
	}

	
	public double  getFractalDimension () {
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for ( double[] vals : distributionLog) 
			obs.add(vals[0], vals[1]);
		return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
	}

	private Box[][] getBoxes (int[] numBox) {
		Box[][] boxes = initBoxes(numBox) ;	
		double [] sizeBox = boxes[0][0].getSize() ;
		for ( Node n : graph.getEachNode() ) {
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			int minX = (int) Math.floor(coords[0]/ sizeBox[0]) ,	
				minY = (int) Math.floor(coords[1]/ sizeBox[1]  );
			Box b = boxes[minX][minY];
			b.putNode(n);
	//		colBox.add(b);
		}
		return boxes;
	}

	private Collection<Double> getCollValues ( double sizeBox ) {	
		int[] numBox = new int [] { ( int ) (sizeSpace[0] / sizeBox)  , ( int ) (sizeSpace[1] / sizeBox)  } ;		
		ArrayList<Double> coll = new ArrayList<Double> () ;
		Box[][] bs = getBoxes(numBox) ; 
//		System.out.print ( "(" ) ;
//		for ( Box b : colBox ) {
		for  ( int  x = 0 ; x < numBox[0] ; x++ ) {
			for  ( int  y = 0 ; y < numBox[1] ; y++ )  {
				double n = (double) bs[x][y].getNumNodes() ;
				if ( n !=0 )
					coll.add( (double) bs[x][y].getNumNodes());
		
			}
		}
		return coll;
	}

	private Box[][] initBoxes ( int[] numBox ) {
		int idInt = 0 ;
		sizeBox = new double[] { (double) sizeSpace[0] / numBox[0] , (double) sizeSpace[1] / numBox[1] };
		Box[][] boxes = new Box[numBox[0]] [numBox[1]]  ;
		for ( int x = 0 ; x < numBox[0] ; x++ ) {
			for ( int y = 0 ; y < numBox[1] ; y++ ) {
				boxes[x][y] = new Box(Integer.toString(idInt++), sizeBox, new double[] { x,y } );
			}
		}
		return boxes;
	}


// GET
// --------------------------------------------------------------------------------------------------------------------------------------------------	
	public ArrayList<double[]> getDistribution(){
		return distribution  ;
	}
	
	public ArrayList<double[]> getLogDistribution(  ){	
		return distributionLog; 
	}
	
	public void exportPointsCsv (boolean run ,String pathStore ) throws IOException {
		if (run ) {
			FileWriter fw = new FileWriter(pathStore , true);
			String header = "size;val;" ;
			expCsv.addCsv_header( fw , header ) ;
			for (double[] point : distribution ) { 
				String[] line = new String[] {String.format("%.3f",point[0]) , String.format("%.3f",point[1])} ;
				expCsv.writeLine(fw, Arrays.asList(  line ) ,';' ) ;	
			}
			fw.close();			
		}
	}
	
	public static double getFractalFromCsv ( String pathCsv ) throws IOException {
		try {
		WeightedObservedPoints obs = new WeightedObservedPoints();	
		BufferedReader br = new BufferedReader(new FileReader(pathCsv));
		String line = br.readLine() ;
		int i = 0 ;
		while ((line = br.readLine()) != null) {
			String[] split = line.split(";") ;
			obs.add(Math.log(Double.parseDouble(split[0])), Math.abs(Double.parseDouble(split[1]) ));
		}
		return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
		} catch (FileNotFoundException e) {
			return 0 ;
		}
	}
	
	public static double getFractalFromCsv_rem ( String pathCsv , int[] ext ) throws IOException {
		try {
			WeightedObservedPoints obs = new WeightedObservedPoints();	
			BufferedReader br = new BufferedReader(new FileReader(pathCsv));
			String line =br.readLine();
			int i = 0 ;
			while ( i < ext[0] ) { 
				line = br.readLine() ; 
				i++ ;		
			}
			double[] size = new double[ext[1] - ext[0]],
					 val  = new double[ext[1] - ext[0]];
			
			while ((line = br.readLine()) != null & i < ext[1] ) {	
				String[] split = line.split(";") ;
				size [i - ext[0]] = Double.parseDouble(split[0]) ;
				val  [i - ext[0]] = Double.parseDouble(split[1]) ;
				i ++ ;
			}			
			int p = 0 ;
			while ( p < size.length -1 ){
//				System.out.println(val[p] - val[p+1]);
				if ( Math.abs(val[p]) - Math.abs(val[p+1]) > 0 ) {
					obs.add(Math.log(size[p]), Math.abs(val[p] ));
				}
				p++;
			}
			return  Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
		}
		 catch (FileNotFoundException e) {
			return 0 ;
		}
	}

	public double getRSquare() {
		// TODO Auto-generated method stub
		return 0;
	}
}
