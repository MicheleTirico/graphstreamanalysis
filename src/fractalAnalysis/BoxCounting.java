package fractalAnalysis;

import graphTool.ReadDGS;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import handleFile.expCsv;
import handleFile.handleFolder;
import simplifyNetwork.Simplifier;

 
public class BoxCounting  {
	private SimpleRegression sr = new SimpleRegression() ;
	private static SimpleRegression sr_rem = new SimpleRegression();
	private double RSquare ; 
	private Graph graph ;
	private int [] sizeSpace ;
	private double [] sizeBox = new double[2] ;
	private double  fd = 0 ;
	Box[][] boxes ;
	/**
	 * 0 -> numBox, 1 -> sizeBox , 2 -> numBox  
	 */
	private ArrayList<double[]> distribution = new ArrayList<double[]>() ,
								distributionLog = new ArrayList<double[]>() ;

	private int[] extNumBox ;
	private int incNumBox ;
	private double[] extSizeBox ;
	private double incSizeBox ; 
	
	public BoxCounting ( Graph graph , int[] sizeSpace) { 
		this.graph = graph ;
		this.sizeSpace = sizeSpace; 
	}

	public void setParamsNumBox(int[] extNumBox ,int inc ) {
		this.extNumBox = extNumBox ;
		this.incNumBox = inc ;
	}
	
	public void setParamsSizeBox(double[] extSizeBox ,double inc ) {
		this.extSizeBox = extSizeBox ;
		this.incSizeBox = inc ;
	}
	
	public void computeNumBox () {
		for ( int numBox = extNumBox[0] ; numBox < extNumBox[1] ; numBox = numBox + incNumBox) {
			double  v0 = numBox , 
					v1  = getMinBox(new int []{numBox ,  numBox })  ;
		//	System.out.println(String.format("%.1f",sizeBox) + " " + String.format("%.2f",v0) + "( "+String.format("%.2f", Math.log(v1) ) + " ) / "  );
			distribution.add(new double[] { v0 , v1 } )  ;
			distributionLog.add(new double[] { v0 ,	Math.log(v0) ,  	Math.log(v1) })  ;
			sr.addData(	Math.log(v0) ,  	Math.log(v1) );
		}
	}
	
	public void computeSizeBox () {
//		System.out.print(Arrays.toString(extSizeBox) +  " " + incSizeBox +  " " + Arrays.toString(sizeBox)) ; 
		for ( double sizeBox = extSizeBox[0] ; sizeBox < extSizeBox[1] ; sizeBox = sizeBox + incSizeBox) {		
//			System.out.println(sizeBox);
			double  v0 = sizeBox , 
					v1 = getMinBox( sizeBox )  ;

//			System.out.print(String.format("%.1f",sizeBox) + " " + String.format("%.5f",v1) + "( "+String.format("%.5f", Math.log(v1) ) + " ) / "  );
			distribution.add(new double[] { v0 , v1 } )  ;
			distributionLog.add(new double[] { 	Math.log(v0) ,  	Math.log(v1) })  ;
			sr.addData(	Math.log(v0) ,  	Math.log(v1) );
		}
	}
	
	public void computeSizeBox (int stop ) {

		double  v0 = 0,  v1 = 0;		
		int stab = 0 ;
		double sizeBox = extSizeBox[0] ;
		while ( sizeBox < extSizeBox[1] && stab < stop ) {
			v0 = sizeBox ; 
			double v1Test = getMinBox( sizeBox )  ;
			if ( v1Test == v1  ) stab ++ ;
			else {
				v1 = v1Test ;
				stab = 0 ; 
			}
			
//			System.out.print(stab + " " + String.format("%.1f",sizeBox) + " " + String.format("%.5f",v1) + "( "+String.format("%.5f", Math.log(v1) ) + " ) / "  );
			distribution.add(new double[] { v0 , v1 } )  ;
			distributionLog.add(new double[] { 	Math.log(v0) ,  	Math.log(v1) })  ;
			sr.addData(	Math.log(v0) ,  	Math.log(v1) );
			sizeBox = sizeBox + incSizeBox ;			
			
		}
	}
	
	public void computeTest ( int iter ) {
		int p = 1 ;
		while ( p < iter) {
			double[] sizeBox = new double[] { extSizeBox[0] / p , extSizeBox[1] / p };
			double numBox = Math.pow(p, 2),
					e = getMinBox(sizeBox) ;
			
//			System.out.println( p +" " + String.format("%.2f",numBox) + " " +e+"( "+ String.format("%.2f",Math.log(numBox))  +String.format("%.2f", Math.log(e)  ) + " ) / "  );
			distribution.add(new double[] { numBox , e} )  ;
			distributionLog.add(new double[] { 	Math.log(numBox) ,  	Math.log(e) })  ;
			sr.addData(	Math.log(numBox) ,  	Math.log(e) );
			p++;
			} 
		}
	

// --------------------------------------------------------------------------------------------------------------------------------------------------	
	private int getMinBox ( int[] numBox ) {
		int numNoEmpty = 0;
		  boxes = getBoxesWithNodes ( numBox ) ;
		for ( int x = 0 ; x < numBox[0] ; x++ ) {
			for ( int y = 0 ; y < numBox[1] ; y++ ) {
				if (! boxes[x][y].isEmpty())
					numNoEmpty++;
			}
		}
		return numNoEmpty ;
	}
	
	public int getMinBox ( double sizeBox ) {
		int numNoEmpty = 0 ;
		int[] numBox = new int [] { ( int ) (sizeSpace[0] / sizeBox)  ,  ( int ) (sizeSpace[1] / sizeBox)    } ;		
		boxes = getBoxesWithNodes ( numBox ) ;
		for ( int x = 0 ; x < numBox[0] ; x++ ) {
			for ( int y = 0 ; y < numBox[1] ; y++ ) {
				if (! boxes[x][y].isEmpty())
					numNoEmpty++;
			}
		}
		return numNoEmpty ;
	}
	
	public int getMinBox ( double[] sizeBox ) {
		int numNoEmpty = 0 ;
		int[] numBox = new int [] { ( int ) (sizeSpace[0] / sizeBox[0])  ,  ( int ) (sizeSpace[1] / sizeBox[1])    } ;		
		boxes = getBoxesWithNodes ( numBox ) ;
		for ( int x = 0 ; x < numBox[0] ; x++ ) {
			for ( int y = 0 ; y < numBox[1] ; y++ ) {
				if (! boxes[x][y].isEmpty())
					numNoEmpty++;
			}
		}
		return numNoEmpty ;
	}
	
	private Box[][] getBoxesWithNodes (int[] numBox) {
		Box[][] boxes = initBoxes(numBox) ;	
//		System.out.println(boxes.length);
		double [] sizeBox = boxes[0][0].getSize() ;
		for ( Node n : graph.getEachNode() ) {
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			int minX = (int) Math.floor(coords[0]/ sizeBox[0]) ,	
				minY = (int) Math.floor(coords[1]/ sizeBox[1]  );
			Box b = boxes[minX][minY];
			b.putNode(n);
		}
		return boxes;
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
	
	public double getRSquare () {
		return RSquare = sr.getRSquare();
	}
	
	public double  getFractalDimension () {
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for ( double[] vals : distributionLog) 
			obs.add(vals[0], vals[1]);
		return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
	}
	
	public ArrayList<double[]> getDistribution(){
		return distribution  ;
	}
	
	public ArrayList<double[]> getLogDistribution(  ){	
		return distributionLog; 
	}
	
	public int getNumMaxBox ( double[] sizeBox) {
//		int n =0 ;
//		n = (sizeSpace[0] / sizeBox[0] )
		return (int) ((sizeSpace[0] / sizeBox[0] ) * (sizeSpace[1] / sizeBox[1] ));
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
				obs.add(Math.log(Double.parseDouble(split[0])), Math.log(Double.parseDouble(split[1]) ));
			}
			return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
		}
		 catch (FileNotFoundException e) {
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
				if ( val[p] - val[p+1] > 0 ) {
					double  x = Math.log(size[p]), y = Math.log(val[p] ) ;
					obs.add(x , y);
					sr_rem.addData(x,y);
				}
				p++;
			}
			System.out.println(sr_rem.getR() );
			return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
		}
		 catch (FileNotFoundException e) {
			return 0 ;
		}
	}
	public static double[] getFractalFromCsv_rem_square ( String pathCsv , int[] ext ) throws IOException {
		
		SimpleRegression sr_rem = new SimpleRegression();
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
				if ( val[p] - val[p+1] > 0 ) {
					double  x = Math.log(size[p]), y = Math.log(val[p] ) ;
					obs.add(x , y);
					sr_rem.addData(x,y);
				}
				p++;
			}
//			System.out.println(sr_rem.getR() + " " +  sr_rem.predict(x));
			return new double[] {Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) , Math.abs( sr_rem.getR() ) }  ;
		}
		 catch (FileNotFoundException e) {
			return  new double[] { 0 ,0 } ;
		}
	}
    
        
        /**
         * compute the box counting of a geometric graph
         * @param args  0 string    the path of the graph
         *              1 string    the path of the csv file (output)
         *              2 Boolean   true = simplify the graph
         */
        public static void main (String[] args ) throws IOException {
            int p = 0 ;
            String pathGraph = args[p++] , pathStore = args[p++];
            boolean simpl = Boolean.parseBoolean(args[p++]);
            
            Graph   g = new ReadDGS(pathGraph).getGraph();
            if (simpl) g = Simplifier.getSimplifiedGraph(g, true);
            
            // move and rescale graph
            GeographicalTools.GeoprocessingTools gt = new GeographicalTools.GeoprocessingTools(g, true);
		
            // move to origin 
            double moveX = gt.getXext()[0] , moveY = gt.getYext()[0] ;
            gt.moveGraph(new double[] {-moveX , -moveY } );

            // rescale
            int[] sizeSpace = new int [] {1000,1000} ;
            double maxSize = Arrays.stream(sizeSpace).min().getAsInt(),
                delta = 0.001 ,  
                scale = maxSize / Math.max(gt.getXext()[1], gt.getYext()[1]) * ( 1 - delta) ;
            
            gt.incremCoords(scale);
            
            // fractal bc
            BoxCounting bc= new BoxCounting( g , sizeSpace ) ;		
            bc.setParamsSizeBox(new double []  { 1  ,  maxSize} , 1 );
            bc.computeSizeBox(); 
            
            System.out.println(" fractal dimension -> " + bc.getFractalDimension()) ; 
            handleFolder.removeFileIfExist(new String[] {pathStore});
            bc.exportPointsCsv(true, pathStore);
            
        }
}
