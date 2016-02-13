/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 *
 * @author dani
 */
public class TestIndex {

  

    public TestIndex() {
    }
    public static void main (String[] args){
	 String inputCollectionPath = "src/es/uam/eps/bmi/clueweb-1K";
        String outputCollectionPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(inputCollectionPath, outputCollectionPath, new HTMLSimpleParser());
        LucIdx.load(outputCollectionPath);
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("src/es/uam/eps/bmi/frecuencias1K.txt");
            pw = new PrintWriter(fichero);

             //for (int i = 0; i < 10; i++)
             //    pw.println("Linea " + i);
             List<String> terms = LucIdx.getTerms();
            
            for (String term : LucIdx.getTerms()) {
                int freqs = 0;
                int ndocs = 0;
                List<Posting> termPostings = LucIdx.getTermPostings(term);
                for (Posting post : termPostings) {
                    freqs = freqs + post.getTermFrequency();
                    ndocs++;
                }
                pw.println(term +" "+ freqs +" "+ ndocs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
           // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
}
    
}
