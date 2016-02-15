/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dani
 */
public class TestSearcher {

    public static void main(String[] args) throws IOException {
        /*if (args.length != 1) {
         System.out.println("Error index_path\n");
         return;
         }*/

        String indexPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(indexPath);
        FileWriter fichero = new FileWriter("src/es/uam/eps/bmi/Querys1K.txt");
	String queryFile = "src/es/uam/eps/bmi/clueweb-1K/queries.txt";
        final PrintWriter pw = new PrintWriter(fichero);

        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            
            
	    int indice=1;
	    int max = 5;
            pw.println(" querys Top "+max);
            for (String query : Files.readAllLines(Paths.get(queryFile))) {
                List<ScoredTextDocument> resul = lucSearch.search(query.substring(2));
                pw.println(query + " -- " + (indice++)+":");
                if (resul != null && resul.size() > 0) {
                    resul.stream().limit(5).forEach((hit) -> pw.println(hit.getDocId()));
                } else {
                    pw.println("Query vacia");
                }
            }
            fichero.close();
        }

    }
}
