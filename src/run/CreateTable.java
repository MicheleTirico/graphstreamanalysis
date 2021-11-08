package run;

import graphTool.ReadDGS;
import handleFile.expCsv;
import java.io.*;
import java.util.*;
import java.util.ArrayList;


import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.apache.commons.io.FilenameUtils;

import analyzers.*;
import analyzers.IndicatorSet.indicator;

/**
 * this script creates a csv file. each row is a graph, each column an indicator.
 * input :  0 the folder which contains the graphs -> "/../..
 *          1 path to store the new file -> "/../namefile.csv"
 * output: a csv file. Each line is a graph and each column an indicator.
 * @author mtirico
 */

public class CreateTable {

  public static void createTable_id (  String path, String pathStore, ArrayList<indicator> listInd ) throws IOException {
    if (new File(pathStore).exists())	new File(pathStore).delete();
    FileWriter fw = new FileWriter(pathStore , true);
    String header = "id,";
    for ( indicator i : listInd) header += i.toString().substring(0, 4) + "," ;
    expCsv.addCsv_header( fw , header ) ;
    int i = 0;
    for (File f : new File(path).listFiles() ) {
      if (FilenameUtils.getExtension(f.getName()).toString().equals("dgs")) {
        System.out.println(f);
        String id = FilenameUtils.removeExtension(f.getName());
        Graph g = new ReadDGS(path+id+".dgs").getGraph(),
        sim = simplifyNetwork.Simplifier.getSimplifiedGraph(g, true);

        IndicatorSet is = new IndicatorSet();
        is.setGraph(sim);
        ArrayList<String> line = new ArrayList<String>();

        //line.add(id.substring(3,8));
        line.add(Integer.toString(i++));
        for ( indicator in : listInd )  	line.add( String.format("%.3f",is.getValue(in))  );
        expCsv.writeLine(fw, line);
        System.out.println(line);

      }
    }
    fw.close();
  }


  public static void createTable_pcpr ( String path, String pathStore, ArrayList<indicator> listInd ) throws IOException {
    if (new File(pathStore).exists())	new File(pathStore).delete();
    FileWriter fw = new FileWriter(pathStore , true);
    String header = "pc;pr;";
    for ( indicator i : listInd) header += i.toString().substring(0, 4) + ";" ;
    expCsv.addCsv_header( fw , header ) ;

    for (File f : new File(path).listFiles() ) {
  //    if (FilenameUtils.getExtension(f.getName()).toString() == ".dgs") {
        String id = FilenameUtils.removeExtension(f.getName());
        Graph g = new ReadDGS(path+id+".dgs").getGraph(),
        sim = simplifyNetwork.Simplifier.getSimplifiedGraph(g, true);

        IndicatorSet is = new IndicatorSet();
        is.setGraph(sim);
        ArrayList<String> line = new ArrayList<String>();
        String pc = g.getAttribute("pc").toString();
        String pr = g.getAttribute("pr").toString();

        line.add(pc);
        line.add(pr);

        for ( indicator in : listInd )  	line.add( String.format("%.3f",is.getValue(in))  );
        expCsv.writeLine(fw, line);
        System.out.println(line);
      }

    fw.close();
  }

  public static void main(String[] args) throws IOException {
    String text = "Parameters: pathData pathStore";
    System.out.println(text);
      int pos = 0 ;
      String path = args[pos++], pathStore = args[pos++];
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
      )) ;

//    createTable_pcpr(path, pathStore ,listInd) ;

      createTable_id(path, pathStore ,listInd) ;
  }
}
