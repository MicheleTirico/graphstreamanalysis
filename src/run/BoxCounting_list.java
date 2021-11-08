
package run;

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

import graphTool.ReadDGS;
import handleFile.expCsv;
import handleFile.handleFolder;
import simplifyNetwork.Simplifier;
import fractalAnalysis.*;

/**
 * create a list of csv, each of them is a DGS
 * input :  0 the folder which contains the graphs -> "/../..
 *          1 path to store the new file -> "/../folder"
 * output: a list of csv files.
 * @author mtirico
 */

public class BoxCounting_list {

    public static void main(String[] args) throws IOException {
        int pos = 0 ;
        String path = args[pos++], pathStore = args[pos++];

        for (File f : new File(path).listFiles() ) {
          if (FilenameUtils.getExtension(f.getName()).toString().equals("dgs")) {
            Graph   g = new ReadDGS(f.getPath()).getGraph();
            g = Simplifier.getSimplifiedGraph(g, true);
            String id = FilenameUtils.removeExtension(f.getName());

            // move and rescale graph
            GeographicalTools.GeoprocessingTools gt = new GeographicalTools.GeoprocessingTools(g, true);

            // move to origin
            double moveX = gt.getXext()[0] , moveY = gt.getYext()[0] ;
            gt.moveGraph(new double[] {-moveX , -moveY } );

            // rescale
  //          int[] sizeSpace = new int [] {(int)Math.pow(2,7),(int)Math.pow(2,7)} ;

            int[] sizeSpace = new int [] {1000,1000} ;
            double maxSize = Arrays.stream(sizeSpace).min().getAsInt(),
                delta = 0.001 ,
                scale = maxSize / Math.max(gt.getXext()[1], gt.getYext()[1]) * ( 1 - delta) ;

            gt.incremCoords(scale);

            // fractal bc
            BoxCounting bc= new BoxCounting( g , sizeSpace ) ;
            bc.setParamsSizeBox(new double []  { 1  ,  maxSize} , 1 );
            bc.computeSizeBox();

            System.out.println(" fractal dimension -> " + bc.getFractalDimension()) ;
            handleFolder.removeFileIfExist(new String[] {            pathStore + id+ "_fractal.csv"});
            bc.exportPointsCsv(true, pathStore + id+ "_fractal.csv");
          }
        }
    }

}
