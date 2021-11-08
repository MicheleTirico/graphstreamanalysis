/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import graphTool.ReadDGS;
import handleFile.expCsv;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

/**
 *
 * @author mtirico
 */
public class ListCoordinates {
   
    /**
    * with this script, you can compute the position of each vertex and the degree of a graph (DGS).
    * input :  0 the path of the graph -> "/../nameGraph.dgs
    *          1 path to store the new file -> "/../namefile.csv"
    * output: a csv file. Each line is the id of the node and the coordinate x and y.
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) throws IOException {
        int pos = 0 ;
        String pathIN = args[pos++], pathOUT = args[pos++];
        System.out.println(pathIN +" " +pathOUT);
        if (new File(pathOUT).exists())	new File(pathOUT).delete();
        FileWriter fw = new FileWriter(pathOUT , true);
        String header = "id;deg;x;y;" ;
        expCsv.addCsv_header( fw , header ) ;
        Graph g = new ReadDGS(pathIN).getGraph();            
        
        for (Node n : g.getEachNode()) {
            String id = n.getId();
            double[] coords = GraphPosLengthUtils.nodePosition(n);
            String line = Integer.toString(n.getDegree())+";"+Double.toString(coords[0]) + ";"+Double.toString(coords[1]);            
            expCsv.writeLine(fw, line);          
        }
        fw.close();
    }
}
