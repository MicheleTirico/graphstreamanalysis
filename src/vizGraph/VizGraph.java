package vizGraph;

import graphTool.ReadDGS;
import java.io.IOException;
import java.io.File;

import java.util.Date;
import org.apache.commons.io.FilenameUtils;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
  A class to visualize the graphs
  input:  path of dgs
  output: path of png
**/
public class VizGraph {

  public static void main(String[] args)  throws IOException {
    String text =
    "Parameters:\n"+
    "Tip: for a basic example, use: pathDgs pathStore sizeNodes sizeEdges colorNodes colorEdges isSimpl setSquare \n" ;

    System.out.println("test the class -> "+ new Object(){}.getClass().getName() + " at " + new Date().toString()  +"\n"+text) ;
    try {
      new VizGraph(args);
    } catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println(    "there is problem with initial parameters.\n" +ex.toString());
    }
  }

  public VizGraph(String[] args ) throws IOException {
    int pos = 0 ;
    String
      pathDgs = args[pos++],
      pathStore = args[pos++];
    double
      sizeNode = Double.parseDouble(args[pos++]),
      sizeEdge = Double.parseDouble(args[pos++]);
    String
      colorNode = args[pos++] ,
      colorEdge = args[pos++];
    Boolean
      isSimpl= Boolean.parseBoolean(args[pos++]),
      setSquare = Boolean.parseBoolean(args[pos++]);

    if (FilenameUtils.getExtension(pathDgs).equals("dgs")) {
      Graph g = new ReadDGS(pathDgs).getGraph();
//      System.out.println(g. 	getAttributeKeySet());
    int size ;
    double pc, pr ;
    String id ;
    try { size = (int) g.getAttribute("size"); } catch (Exception ex ) { size = 7 ;}
    try {
      pr = (double) g.getAttribute("pr"); pc = (double) g.getAttribute("pc");
      id = "noRd_"+String.format("%.4f",pc) +"_"+String.format("%.4f",pr) +".png";
    } catch (Exception ex) {
      id =  FilenameUtils.removeExtension(new File(pathDgs).getName());
    }
      if (isSimpl) g = simplifyNetwork.Simplifier.getSimplifiedGraph(g, true);
      g.setAttribute("ui.stylesheet", "node {	size: "+sizeNode+"px; fill-color:"+colorNode+";} edge { 	size: "+sizeEdge+"px; fill-color:"+colorEdge+";}");
      if (setSquare) createSquare(g,true, Math.pow(2,size), 0);
      g.display(false);
      g.addAttribute("ui.screenshot",  pathStore +id+".png" ) ;
      System.out.println("go to -> "+ pathStore+id);

    } else {    System.out.println("the file is not OK !"); }
  }


  public static void createSquare (Graph g , boolean run , double XYmax , double XYmin ) {
    if ( run ) {
      Node n00 = g.addNode("0b00");
      n00.addAttribute("xyz", XYmin , XYmin, 0 );
      n00.setAttribute("scale", true);
      Node n10 = g.addNode("0b10");
      n10.addAttribute("xyz", XYmax , XYmin , 0 );
      n10.setAttribute("scale", true);

      Node n01 = g.addNode("0b01");
      n01.addAttribute("xyz", XYmin , XYmax , 0 );
      n01.setAttribute("scale", true);

      Node n11 = g.addNode("0b11");
      n11.addAttribute("xyz", XYmax , XYmax , 0 );
      n11.setAttribute("scale", true);

      g.addEdge("0bord0", n00, n10) ;
      g.addEdge("0bord1", n00, n01) ;
      g.addEdge("0bord2", n01, n11) ;
      g.addEdge("0bord3", n10, n11) ;
    }
  }
}
