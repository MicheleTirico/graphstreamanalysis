package run;

import java.util.*;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import vizGraph.VizGraph;
import graphTool.ReadDGS;
import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.*;


public class GetImages_list {

  public static void main(String[] args)  throws IOException {
    System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    try {
      FileSinkImages pic = new FileSinkImages(OutputType.png, Resolutions.QSXGA);
      pic.setLayoutPolicy(LayoutPolicy.NO_LAYOUT ) ; //COMPUTED_ONCE_AT_NEW_IMAGE );
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

      for (File f : new File(pathDgs).listFiles() ) {
        if (FilenameUtils.getExtension(f.getName()).toString().equals("dgs")) {
          String name = FilenameUtils.removeExtension(f.getName());
          String id = name; // name.substring(0,name.length() - 8);
          System.out.println(id);
          Graph g = new ReadDGS(f.getPath()).getGraph();
          g.addAttribute("ui.quality");
          g.addAttribute("ui.antialias");
          int size ;
          try { size = (int) g.getAttribute("size"); } catch (Exception ex ) { size = 7 ;}
          if (isSimpl) g = simplifyNetwork.Simplifier.getSimplifiedGraph(g, true);
          g.setAttribute("ui.stylesheet", "node {	size: "+sizeNode+"px; fill-color:"+colorNode+";} edge { 	size: "+sizeEdge+"px; fill-color:"+colorEdge+";}");
          if (setSquare) VizGraph.createSquare(g,true, Math.pow(2,size), 0);
//          g.display(false);
        //  g.addAttribute("ui.screenshot",  pathStore +id+".png" ) ;
          pic.writeAll(g,pathStore+id+".png");
          System.out.println("go to -> "+ pathStore+id);
          }
      }
    } catch (Exception ex) {
      String text =
      "Parameters:\n"+
      "Tip: for a basic example, use: pathDgs pathStore sizeNodes sizeEdges colorNodes colorEdges isSimpl setSquare \n" ;
      System.out.println("test the class -> "+ new Object(){}.getClass().getName() + " at " + new Date().toString()  +"\n"+text) ;
      System.out.println(ex);
    }
  }

}
