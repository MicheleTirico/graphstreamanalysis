package analyzers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import analyzers.IndicatorSet.indicator;
import graphTool.GraphTool;
import graphgenerator.GabrielGenerator;
import handleFile.expCsv;
import handleFile.handleFolder;

public class AnalyseGraph {
	private String id ; 
	private Graph g ;
	private ArrayList<indicator> listInd;
//	private Map<indicator, Double> mapIndVals = new HashMap<indicator, Double> () ;
	private Map<indicator, Double> mapIndVals = new HashMap<indicator, Double> () ;

	private IndicatorSet iS ;

	public AnalyseGraph (String id , Graph g , ArrayList<indicator> listInd) {
		this.listInd = listInd ;
		this.g = g ;
		this.listInd = listInd ;
		iS = new IndicatorSet() ;
		iS.setGraph(g);
		compute();
	}
	public void printMap () {
		for (indicator in : mapIndVals.keySet()) {
			System.out.println(in + " -> " + mapIndVals.get(in));
		}
	}
	
	public Map getMap ( ) { return mapIndVals ; }
	
	public void compute () {
		for ( Node n : g.getEachNode()) {
			double d = n.getDegree();
			n.addAttribute("degree", d);
		}
		
		for ( Edge e : g.getEachEdge() ) {
			double dist = GraphTool.getDistGeom(e.getNode0(), e.getNode1());
			e.addAttribute("length", dist);		
		}
		for ( indicator in : listInd ) 	{
			double val = iS.getValue(in);
			mapIndVals.put(in, val);
		}
	}
	
	public void exportCsv (String pathStore) throws IOException {
		IndicatorSet iS = new IndicatorSet() ;
		handleFolder.removeFileIfExist(new String[] {pathStore});
		String header = "id;";		
		for ( indicator i : listInd) header += i.toString().substring(0, 4) + ";" ;
		System.out.println(header);
		FileWriter 	fw = new FileWriter(pathStore , true) ;
		expCsv.addCsv_header( fw , header ) ;
		
		ArrayList<String> arr = new ArrayList<String>(Arrays.asList("init")) ;
		iS.setGraph(g);
		for ( indicator in : listInd )  	arr.add( String.format("%.3f",iS.getValue(in))  );
		expCsv.writeLine(fw, arr, ';' ) ;
		System.out.println(arr);
		fw.close();
		
		
	}

	public int [] getDegreeDistr ( ) {
		return Toolkit.degreeDistribution(g);
	}
	
	public static void main(String[] args) {
		System.out.print(new Object(){}.getClass().getName());
		GabrielGenerator gg = new GabrielGenerator("test", 10, 100, new int[] {10,10});
		gg.compute();
		Graph g = gg.getGraph();
		g.display(false);
		ArrayList<indicator> listInd = new ArrayList<indicator> ( Arrays.asList(
				indicator.nodeCount 
				, indicator.averageDegree 
				, indicator.edgeCount
				,indicator.totalEdgeLength
				,indicator.averageLen
				, indicator.gammaIndex ,
			indicator.organicRatio,
			indicator.meshedness, 
			indicator.cost 
		//	,indicator.efficiencyTopo
		//	indicator.efficiencyGeom
		//	, indicator.fractalDimensionBox
			)) ;
		AnalyseGraph ag = new AnalyseGraph("test", g, listInd);
//		System.out.println(ag.getMap() );
		ag.printMap();
		
	}
	

}
