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

import analyzers.IndicatorSet.indicator;
import graphTool.GraphTool;
import graphTool.ReadDGS;
import simplifyNetwork.Simplifier;

public class VizGraph {

  private Graph g ;
  private static String pathGraph, pathStore ;
  private static int sizeNode, sizeEdge;

  public VizGraph (String pathGraph , String pathStore ) throws IOException  {
    this.pathGraph = pathGraph ;
    this.pathStore = pathStore;

    g = new ReadDGS(pathGraph).getGraph();
    g.display(false);
    String colorNode = "black", colorEdge = "black";
    double sizeNode = 5.0;
    double sizeEdge = 0.5 ;

    g.setAttribute("ui.stylesheet", "node {	size: "+sizeNode+"px; fill-color:"+colorNode+";} edge { 	size: "+sizeEdge+"px; fill-color:"+colorEdge+";}");
    for (Node n : g ) {

      n.addAttribute("deg", n.getDegree());
    }
  }

  public static void main (String[] args) throws IOException {
    int p = 0 ;
    pathGraph = args[p++] ;
    pathStore = args[p++] ;

    new VizGraph (pathGraph, pathStore );

  }
}
