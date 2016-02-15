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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


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
	boolean regen = true;
	
        LuceneIndex LucIdx = regen ?  new LuceneIndex(inputCollectionPath, outputCollectionPath, new HTMLSimpleParser()) : new LuceneIndex(outputCollectionPath) ;
        
	FileWriter fichero = null;
        try {
            fichero = new FileWriter("src/es/uam/eps/bmi/frecuencias1K.txt");
        } catch (IOException ex) {
            Logger.getLogger(TestIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        final PrintWriter pw = new PrintWriter(fichero);;
        try {
            

            // term -> (freq, ndoc, tf,idf)
	    LinkedHashMap <String,double[]> freqHash = new LinkedHashMap<>();
            
                LucIdx.getTerms().stream().forEach((term) -> {
                double[] val;
                val = new double[] {0,0,0,0};
                List<Posting> termPostings = LucIdx.getTermPostings(term);
                termPostings.stream().forEach((post) -> {
                    val[0] += post.getTermFrequency();
                    val[1]++;
                    val[2] += post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency())/Math.log(2);
                });
                // val 2 = tf
                val[2] = val[2]/LucIdx.getNumDoc();
                // val 3 = idf
                val[3] = Math.log(LucIdx.getNumDoc()/val[1]);
                freqHash.put(term,val);
             });
            
            List<Entry<String,double[]>> sorted = new LinkedList <>(freqHash.entrySet());
            Collections.sort(sorted, (Entry<String, double[]> o1, Entry<String, double[]> o2) -> Double.compare(o2.getValue()[0],o1.getValue()[0]));
            
            sorted.stream().forEach((val) -> {
                pw.write(String.format("%s %f %f %f %f\n", val.getKey(), val.getValue()[0],val.getValue()[1],val.getValue()[2],val.getValue()[3]));
            });
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
