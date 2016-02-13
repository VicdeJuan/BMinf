/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        String outputCollectionPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(indexPath);
        FileWriter fichero = null;
        PrintWriter pw = null;

        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            /*
             //ahora leemos de teclado las querys
             System.out.println("Introducir las palabras de la búsqueda:");
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             String query = br.readLine();
             */
            ArrayList<String> querys = new ArrayList<>();
            querys.add("obama family tree");
            querys.add("french lick resort and casino");
            querys.add("getting organized");
            querys.add("toilet");
            querys.add("mitchell college");
            fichero = new FileWriter("src/es/uam/eps/bmi/Querys1K.txt");
            pw = new PrintWriter(fichero);
            int indice=1;
            pw.println(" querys Top 10:");
            for (String query : querys) {
                List<ScoredTextDocument> resul = lucSearch.search(query);
                
                pw.println(indice+":");
                if (resul != null && resul.size() > 0) {
                    
                    for (ScoredTextDocument score: resul){
                        
                        pw.println(score.getDocId());
                    }
                    /*resul.stream().forEach((ScoredTextDocument hit) -> {
                        final String aux=hit.getDocId();
                        //pw.println(aux);
                        
                        //System.out.println(hit.getDocId());
                    });
                     */       
                } else {
                    pw.println("Query vacia");
                }
                indice++;
            }
            fichero.close();
        }

    }
}
