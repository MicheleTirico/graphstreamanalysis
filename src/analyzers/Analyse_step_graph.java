package analyzers;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

import analyzers.IndicatorSet.indicator;
import graphTool.ReadDGS;
import handleFile.expCsv;
import handleFile.handleFolder;
import simplifyNetwork.Simplifier;

public class Analyse_step_graph {

 
	private ArrayList<indicator> listInd ;
	private Graph g ;
	private FileSource fs ;
 	private int stepToCompute , stepToStop ;
	private String pathStart , pathStep ;
	private IndicatorSet iS = new IndicatorSet() ;
	private Map<indicator, ArrayList<Double>> mapInd = new HashMap<indicator, ArrayList<Double>>();
	
	public Analyse_step_graph ( ArrayList<indicator> listInd , int stepToCompute, int  stepToStop ) throws IOException {
		this.listInd = listInd ; 
		this.stepToCompute = stepToCompute ;
		this.stepToStop = stepToStop ;
 	}
	
	public void setPaths ( String pathStart , String pathStep ) throws IOException {
		 this.pathStart = pathStart ;
		 this.pathStep = pathStep ;
			init_start();
			init_Step();
	}
	
	public String getSteps ( ) {
		String s = "" ;
		double pos = 0 ;
		while ( pos <= stepToStop / stepToCompute ) 
			s= s + String.format("%.0f",pos++ * stepToCompute ) + ";";
		return s ;
	}
	
	private void init_start ( ) throws IOException {
		fs = new FileSourceDGS();
		g = new SingleGraph("id");
		g.setStrict(false);
		fs.addSink(g);	
		fs.readAll(pathStart);	
		for ( indicator i : listInd )   mapInd.put(i, new ArrayList<Double>() );
	}
	
	private void init_Step ( ) throws IOException {
		fs.begin(new GZIPInputStream(new FileInputStream(pathStep)));
		for ( indicator i : mapInd.keySet() )   mapInd.put(i, new ArrayList<Double>() );
		iS.setGraph(g);
	}
		
	public void nocompute () throws IOException {
 		int t = 0 ;
 		g.display(false);
 		try {
			while (t<= stepToStop && fs.nextStep() ) {
				if (t / (double) stepToCompute - (int) (t / (double) stepToCompute) < 0.0001) { //			
					System.out.print(t + " / ");
//					Graph sim = Simplifier.getSimplifiedGraph(GraphTool.getGiantGraph(g, true), true ) ;
				 
	 					Graph sim  = Simplifier.getSimplifiedGraph( g  , true )  ;						
						iS.setGraph(sim);
						for ( indicator in : mapInd.keySet() ) {
							ArrayList<Double> vals = mapInd.get(in);
//							System.out.println(vals);
//							vals.add(iS.getValue(in));
	//						System.out.println(vals);
//							mapInd.put(in, vals) ;
						}						
				}
			  
				t++ ;
			}
		} catch (EOFException | NullPointerException  e) {  	}
 		stepToStop = t; 
	}
	
	public void compute() throws IOException {
		double t = 0 ;
//		g.display(false); 
	
		try {
			while ( fs.nextStep() && t < stepToStop  ) {
					t = g.getStep() ;
					if (t / (double) stepToCompute - (int) (t / (double) stepToCompute) < 0.0001) {
						System.out.println(t);
						Graph sim  = Simplifier.getSimplifiedGraph( g  , true )  ;						
						iS.setGraph(sim);
		 				for ( indicator in : mapInd.keySet() ) {
							ArrayList<Double> vals = mapInd.get(in);
							vals.add(iS.getValue(in));
		
							mapInd.put(in, vals) ;
						}	
		
					}
			}
		} catch (NullPointerException e) {		}
	}
	
	public void testcompute() throws IOException {
		int t = 0 ;
//		g.display(false); 
		
		while ( t <= stepToStop  ) {
			if (t / (double) stepToCompute - (int) (t / (double) stepToCompute) < 0.0001) {
				System.out.print(t + " / ");

				g = new ReadDGS(pathStart, pathStep, t).getGraph() ; 
				Graph sim  = Simplifier.getSimplifiedGraph( g  , true )  ;			
//				System.out.println(sim.getNodeCount());
				iS.setGraph(sim);
 				for ( indicator in : mapInd.keySet() ) {
					ArrayList<Double> vals = mapInd.get(in);
					vals.add(iS.getValue(in));
					mapInd.put(in, vals) ;
				}	
			}
			t++; 
		}
		
		 
	}
	
	
	public Graph getGraph ( ) { return g ; } 
	public ArrayList<Double>  getIndStep( indicator in) { return mapInd.get(in) ; }
	public Map<indicator, ArrayList<Double>> getMapStep ( ) { return mapInd ; }
	
	public void exportMap ( String pathExport) throws IOException {
		handleFolder.removeFileIfExist(new String[] {pathExport });
		String header = "ind;0;";		
		int step = 0 ; 
//		System.out.println(step + " " + stepToStop + " " + stepToCompute);
		while (step <= stepToStop - stepToCompute ) {
			header +=Integer.toString(step+stepToCompute) + ";";
			step += stepToCompute; 
		}
		
		
		System.out.println(header);
		FileWriter 	fw = new FileWriter(pathExport , true) ;
		expCsv.addCsv_header( fw , header ) ;
		for ( indicator i : mapInd.keySet()) {
			String vals = i.toString() + ";";
			for (Double val : mapInd.get(i) ) {
				vals += String.format("%.3f",val)  + ";";
			}
			expCsv.writeLine(fw, vals);
		}
		fw.close();
		System.out.println("finish / go to -> " + pathExport);
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.print(new Object(){}.getClass().getName());	
		ArrayList<indicator> listInd = new ArrayList<indicator> ( Arrays.asList(
				  indicator.nodeCount 
			)) ;
		
		test2(listInd);
		
	}
	
	private static void test (ArrayList<indicator> listInd ) throws IOException {
		int stepToCompute = 100 , stepToStop = 20000 ;
		String 	path = "data/fb_t_5000_inc_9/" ,
				pathStart = path + "start.dgs"  , pathStep  = path + "fb_act_diff_mazes_step.dgs.zip",
				pathExport = path+ "test_ind_step.csv";
	
//		ReadDGS rg = new ReadDGS(pathStart,pathStep,20000);
//		rg.getGraph().display(false);
// 		System.out.println("ciao");
//		
		Analyse_step_graph asg = new Analyse_step_graph(listInd, stepToCompute, stepToStop  );
		asg.setPaths(pathStart, pathStep);
		asg.compute();
//		asg.testCompute();
 		System.out.println(pathStep);
		System.out.println(asg.getMapStep());
		System.out.println(asg.getIndStep(indicator.nodeCount));
		ArrayList<Double> x = (ArrayList<Double>) Stream.iterate(0.0, n -> n + stepToCompute)
                .limit(stepToStop/stepToCompute+1)
                .collect(Collectors.toList());
		
		ArrayList<Double> y = asg.getIndStep(indicator.nodeCount) ;
		asg.exportMap(pathExport);	 			

	}
	

	private static void test2 (ArrayList<indicator> listInd ) throws IOException {
		int stepToCompute = 100 , stepToStop = 5000 ;
		String 	path = "data/noFb/dgs/"  , // fb_t_5000_inc_9/" ,
				pathStart = path + "start.dgs"  , pathStep   , 
				pathExport = path + "results" ; // test/test_ind_step.csv";
	
  		String [] patterns = {"equilibrium" , "movingSpots" , "mazes", "solitons" } ; 

	for ( String nameSim : new String [] {"noFb" }) {// "fb_act_fk" , "fb_act_diff", "fb_act_morp", "fb_inh_fk", "fb_inh_diff" }) {
		 
		// analyse steps 	
		  
//		 			listInd = new ArrayList<indicator> ( Arrays.asList(indicator.nodeCount )) ;
		 			listInd = new ArrayList<indicator> ( Arrays.asList(
		 					  indicator.nodeCount 
//		 					, indicator.averageDegree 
//		 					, indicator.edgeCount
//		 					, indicator.totalEdgeLength
////		 					, indicator.averageLen
//		 					, indicator.gammaIndex 
//		 					, indicator.organicRatio
//		 					, indicator.meshedness
//		 					, indicator.cost 
		 				)) ;
					for ( String pattern : patterns ) {
						pathStep = path + nameSim + "_" + pattern + "_step.dgs.zip" ;
						System.out.println(pathStep);
						try {			
//							ReadDGS rg = new ReadDGS(pathStart, pathStep, 100000);
//							rg.getGraph().display(false);
							Analyse_step_graph asg = new Analyse_step_graph(listInd, stepToCompute, stepToStop  );
							asg.setPaths(pathStart, pathStep);
							asg.testcompute();	
							asg.exportMap(pathExport + nameSim +"_"+ pattern +"_step.csv");	 			
						} catch (FileNotFoundException ex) { System.out.println("pattern didn't find");		}


					}
				}
			}
	
 
}
