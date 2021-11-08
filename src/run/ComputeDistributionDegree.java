
package run;

import graphTool.ReadDGS;
import handleFile.expCsv;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.apache.commons.io.FilenameUtils;

/**
 * with this script, you can compute the degree distribution of a set of graphs (DGS).
 * input :  0 the folder which contains the graphs -> "/../..
 *          1 path to store the new file -> "/../namefile.csv"
 * output: a csv file. Each line is a graph and each column the degree.
 * @author mtirico
 */

public class ComputeDistributionDegree {

    public static void main(String[] args) throws IOException {
        int pos = 0 ;
        String path = args[pos++], pathStore = args[pos++];
        System.out.println(path +" " +pathStore);
        if (new File(pathStore).exists())	new File(pathStore).delete();
        FileWriter fw = new FileWriter(pathStore , true);
  //      String header = "id;" ;
  //      for (int i = 0 ; i < 10; i++ ) header += Integer.toString(i) + ";" ;
  //      expCsv.addCsv_header( fw , header ) ;
      System.out.println(path + " " + pathStore);
        for (File f : new File(path).listFiles() ) {
          if (FilenameUtils.getExtension(f.getName()).toString().equals("dgs")) {
            String id = FilenameUtils.removeExtension(f.getName());
            Graph g = new ReadDGS(path+id+".dgs").getGraph();
            g = simplifyNetwork.Simplifier.getSimplifiedGraph(g, true);
            int [] deg_01 = Toolkit.degreeDistribution(g);
//            int[] deg = deg_01;
            int[] deg = new int[20];
           for (int i = 0 ; i < deg.length; i++ ) deg[i] = 0;
            for (int i = 0 ; i < deg_01.length; i++ ) deg[i] = deg_01[i];
            float nodeCount = (float) g.getNodeCount() ;
//            String line = id.substring(0, id.length()-9) +";";
            String line = id + ";";
            for (int i = 0 ; i < deg.length; i++ ) line += String.format("%.3f",deg[i] / nodeCount ) + ";";
            expCsv.writeLine(fw, line);
            System.out.println(line);
        }
      }
        fw.close();
    }

}
