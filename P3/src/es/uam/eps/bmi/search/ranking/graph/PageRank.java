package es.uam.eps.bmi.search.ranking.graph;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import es.uam.eps.bmi.search.Utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageRank {

    private String fileOfLinks;
    private Matrix matrix;
    
    public PageRank(String fileOfLinks) {
        FileReader fr = null;
        
        try {
            fr = new FileReader(fileOfLinks);

            BufferedReader br = new BufferedReader(fr);
            
            String line;

            while ((line = br.readLine()) != null) {
                String[] _links = line.split(Utils.InternPostingSeparator);
                if (_links.length <= 2)
                    continue;
                List<String> links = Arrays.asList(_links);
                
                
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("No se ha encontrado el archivo " + fileOfLinks);
        } catch (IOException ex) {
            Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    public double getScoreOf(String docId) {
        return 0.0;
    }

    private double _getScoreOfFrom(String docId, String linksFile) {
        return 0.0;
    }

}
