package analyzers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import graphTool.GraphTool;
import graphTool.ReadDGS;
import handleFile.expCsv;
import handleFile.handleFolder;
import simplifyNetwork.Simplifier;

public class Robustness {
	private String id ;
	private	Graph g ;
	private int numberOfTest;
	private double percentAtEachStep , perMax;
	private Map <Integer, ArrayList<Integer>> mapItSize = new HashMap<Integer, ArrayList<Integer>> ();


	public Robustness (String id , Graph g, int numberOfTest, double percentAtEachStep , double perMax) {
		this.id= id;
		this.g = g ;
		this.numberOfTest = numberOfTest ;
		this.percentAtEachStep = percentAtEachStep ;
		this.perMax = perMax ;
	}

	private ArrayList<Integer> compute_one_iter () {
		int numNodeToRemove = (int) ( g.getNodeCount() * percentAtEachStep ) ;
		double 	per = 0 ;
		ArrayList<Integer> sizeGiant = new ArrayList<Integer> ();
		Graph replay = GraphTool.getGraphReplay("rep", g, false,false) ;
		Collection<Node> nodeSet = replay.getNodeSet();
		ArrayList<Node> test = new ArrayList<Node> (nodeSet) ;
		Collections.shuffle(test);
		int step = 0 ;
		sizeGiant.add(GraphTool.getGiantGraph(replay, false).getNodeCount());
		while ( per < perMax ) {
			int numNode=0 ;
			while (numNode < numNodeToRemove) {
				try {
					replay.removeNode(test.get(step + numNode));
					numNode++ ;
				} catch (IndexOutOfBoundsException e) { numNode++;  }
			}
			step = step + numNodeToRemove ;
			int val = 0 ;
			try {
				val = GraphTool.getGiantGraph(replay, false).getNodeCount();
			} catch (NullPointerException e) { val = 0 ;  	}
			sizeGiant.add(val);
			per = per + percentAtEachStep;
		}
		return sizeGiant ;
	}

	public void compute () {
		int p = 0 ;
		while (p < numberOfTest ) {
			ArrayList<Integer> sizeGiant =compute_one_iter() ;
			mapItSize.put(p ,sizeGiant);
			System.out.println(p +" at " + new Date().toString() + " " + sizeGiant);
			p++;
		}
	}

	public Map getMap () { return mapItSize ; }

	public void exportMap (String pathStore) throws IOException {
		handleFolder.removeFileIfExist(new String[] {pathStore});
		FileWriter 	fw = new FileWriter(pathStore , true) ;
		String header = "id;0.0;" ;
		double 	per = 0  ;
		while ( per < perMax ) {per = per + percentAtEachStep;header += String.format("%.3f",per)  + ";"; }
		expCsv.addCsv_header( fw , header ) ;
		for (Integer map : mapItSize.keySet()) {
			ArrayList<Integer> vals = mapItSize.get(map);
			String t = map+";";
			for ( Integer val : vals ) t+= Integer.toString(val) +";";
			expCsv.writeLine(fw, t);
		}
		fw.close();
		System.out.println("finish at "+ new Date().toString() +" / go to -> " + pathStore);
	}

	public void getRob(double percent ){

	}

	public static void main(String[] args) throws IOException {
		int pos = 0 ;
 		String path = args[pos++], pathStore =args[pos++];
//		String path = "/home/mtirico/git/graphanalysis/data/cities_dgs/codah.dgs" ;
//		String path = "/home/mtirico/git/graphanalysis/data/fecamp/dgs/fecamp_streets.dgs" ;
//		String pathStore  = "/home/mtirico/git/graphanalysis/data/fecamp/results/fecamp_init_rob.csv";

		Graph 	g = new ReadDGS(path).getGraph() ,
				sim = Simplifier.getSimplifiedGraph(g, true);
//				big = GraphTool.getGiantGraph(sim, false);

//		sim.display(false);
		System.out.println("info -> " + sim.getNodeCount() );

		Robustness rn = new Robustness("test", sim, 100, 0.01, 0.8) ;
		rn.compute();
//		System.out.println(rn.getMap());
//		System.out.println(rn.getAverage());
		rn.exportMap(pathStore);
	}

}
