package fractalAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.graphstream.graph.Graph;

import analyzers.Indicators;
import graphTool.GraphTool;
 import handleFile.expCsv;

public class CorrelationDimension extends GraphTool{
	private SimpleRegression sr = new SimpleRegression() ;
	private double RSquare ; 
	private Graph graph ;
	private int [] sizeSpace ;
	private double [] sizeBox = new double[2] ; 
	private double  fd = 0 ;
	
	double [] numBuckets ;
	class bucket {
		
	}
	
	private ArrayList<double[]> distribution = new ArrayList<double[]>() ,
								distributionLog = new ArrayList<double[]>() ;
  ;
 	private double[] extSize ;
	private double incSize  ; 	
	Collection<Box> colBox = new HashSet<Box> () ;

	
	public CorrelationDimension  ( Graph graph , int[] sizeSpace) { 
		this.graph = graph ;
		this.sizeSpace = sizeSpace; 
	}
	
	public void setParamsSizeBox(double[] extSize ,double inc ) {
		this.extSize= extSize;
		this.incSize = inc ;
	}

	public void compute ( ) {
 		int nc = graph.getNodeCount();
 		for ( double sizeBox = extSize [0] ; sizeBox <= extSize [1] ; sizeBox = sizeBox + incSize ) {
			double sum = Indicators.getHeavisideStepFunction (graph.getNodeSet() , sizeBox) ,
					e = ( 2.0 / ( nc * (nc -1) ) ) *   sum ;		
			distribution.add(new double[] { sizeBox, e } )  ;
			double log = ( Math.log(e) );
			if (Double.isInfinite(log) ) e = 0.00001 ;
			distributionLog.add(new double[] { 	Math.log(sizeBox) ,  Math.log(e) })  ;
			sr.addData(	Math.log(sizeBox) ,  Math.log(e) );
//			System.out.println(graph.getId() + " / " + String.format("%.1f",sizeBox) + " " + String.format("%.2f",e) + "( "+String.format("%.2f",  Math.log(e)) + " ) / " + new Date().toString() );

		}
	}
	
//	public void computeBuckets ( int[] numBuckets ) {
//		BucketSet bs = new BucketSet(graph, numBuckets, sizeSpace);
//		int nc = graph.getNodeCount();
// 		for ( double sizeBox = extSize [0] ; sizeBox <= extSize [1] ; sizeBox = sizeBox + incSize ) {
//			double sum = Indicators.getHeavisideStepFunction ( graph.getNodeSet() , sizeBox , bs ) ,
//					e = ( 2.0 / ( nc * (nc -1) ) ) *   sum ;		
//			distribution.add(new double[] { sizeBox, e } )  ;
//			double log = ( Math.log(e) );
//			if (Double.isInfinite(log) ) e = 0.00001 ;
//			distributionLog.add(new double[] { 	Math.log(sizeBox) ,  Math.log(e) })  ;
//			sr.addData(	Math.log(sizeBox) ,  Math.log(e) );
//			System.out.print(String.format("%.1f",sizeBox) + " " + String.format("%.2f",e) + "( "+String.format("%.2f",  Math.log(e)) + " ) / "  );
//
//		}
//	}
	
	public double  getFractalDimension () {
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for ( double[] vals : distributionLog) 
			obs.add(vals[0], vals[1]);
		return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
	}

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

// GET
// --------------------------------------------------------------------------------------------------------------------------------------------------	
	public ArrayList<double[]> getDistribution(){
		return distribution  ;
	}
	
	public ArrayList<double[]> getLogDistribution(  ){	
		return distributionLog; 
	}
		
	public double getRSquare () {
		return RSquare = sr.getRSquare();
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
				double e = Double.parseDouble(split[1]) ;
				double log = ( Math.log(e) );
				if (Double.isInfinite(log) ) e = 0.00001 ;
				
				obs.add(Math.log(Double.parseDouble(split[0])), Math.log(e ));
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
				double e = Double.parseDouble(split[1]) ;
				double log = ( Math.log(e) );
				if (Double.isInfinite(log) ) e = 0.00001 ;
				obs.add(Math.log(Double.parseDouble(split[0])), Math.log(e ));
//				size [i - ext[0]] = Double.parseDouble(split[0]) ;
//				val  [i - ext[0]] =  Math.log(e) ;
				i ++ ;
			}			
//			int p = 0 ;
//			while ( p < size.length -1 ){
//				if ( Math.abs(val[p]) - Math.abs(val[p+1]) > 0 ) 
//					obs.add(Math.log(size[p]), val[p] );
//				p++;
//			}
			return Math.abs(PolynomialCurveFitter.create(1).fit(obs.toList())[1] ) ;
		}
		 catch (FileNotFoundException e) {
			return 0 ;
		}
	}

}
