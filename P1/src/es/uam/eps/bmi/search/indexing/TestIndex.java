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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author dani
 */
public class TestIndex {

  

    public TestIndex() {
    }
    public static void main (String[] args){
	 String inputCollectionPath_1 = "src/es/uam/eps/bmi/clueweb-1K";
         String inputCollectionPath_10 = "src/es/uam/eps/bmi/clueweb-10K";
        String outputCollectionPath_1 = "outputCollection_1";
        String outputCollectionPath_10 = "outputCollection_10";
	
        boolean regen = false;
	
        LuceneIndex LucIdx_1 = regen ?  new LuceneIndex(inputCollectionPath_1, outputCollectionPath_1, new HTMLSimpleParser()) : new LuceneIndex(outputCollectionPath_1) ;
        LuceneIndex LucIdx_10 = regen ?  new LuceneIndex(inputCollectionPath_10, outputCollectionPath_10, new HTMLSimpleParser()) : new LuceneIndex(outputCollectionPath_10) ;
        
	FileWriter fichero_1 = null;
        FileWriter fichero_10 = null;
        try {
            fichero_1 = new FileWriter("src/es/uam/eps/bmi/frecuencias1K.txt");
            fichero_10 = new FileWriter("src/es/uam/eps/bmi/frecuencias1K.txt");
        } catch (IOException ex) {
            Logger.getLogger(TestIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        final PrintWriter pw_1 = new PrintWriter(fichero_1);    
        final PrintWriter pw_10 = new PrintWriter(fichero_10);
        try {
            // term -> (freq, ndoc, tf,idf)
	    final LinkedHashMap <String,double[]> freqHash = new LinkedHashMap<>();
            
                LucIdx_1.getTerms().stream().forEach((term) -> {
                double[] val;
                val = new double[] {0,0,0,0};
                List<Posting> termPostings = LucIdx_1.getTermPostings(term);
                termPostings.stream().forEach((post) -> {
                    val[0] += post.getTermFrequency();
                    val[1]++;
                    val[2] += post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency())/Math.log(2);
                });
                // val 2 = tf
                val[2] = val[2]/LucIdx_1.getNumDoc();
                // val 3 = idf
                val[3] = Math.log(LucIdx_1.getNumDoc()/val[1]);
                freqHash.put(term,val);
             });
            
            List<Entry<String,double[]>> sorted = new LinkedList <>(freqHash.entrySet());
            Collections.sort(sorted, (Entry<String, double[]> o1, Entry<String, double[]> o2) -> Double.compare(o2.getValue()[0],o1.getValue()[0]));
            
            sorted.stream().forEach((val) -> {
                pw_1.write(String.format("%s %f %f %f %f\n", val.getKey(), val.getValue()[0],val.getValue()[1],val.getValue()[2],val.getValue()[3]));
            });
            
            
            
            // term -> (freq, ndoc, tf,idf)
	    final LinkedHashMap <String,double[]> freqHash_10 = new LinkedHashMap<>();
            
                LucIdx_10.getTerms().stream().forEach((term) -> {
                double[] val;
                val = new double[] {0,0,0,0};
                            List<Posting> termPostings = LucIdx_10.getTermPostings(term);
                termPostings.stream().forEach((post) -> {
                    val[0] += post.getTermFrequency();
                    val[1]++;
                    val[2] += post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency())/Math.log(2);
                });
                // val 2 = tf
                val[2] = val[2]/LucIdx_10.getNumDoc();
                // val 3 = idf
                val[3] = Math.log(LucIdx_10.getNumDoc()/val[1]);
                freqHash_10.put(term,val);
             });
            
            sorted = new LinkedList <>(freqHash.entrySet());
            Collections.sort(sorted, (Entry<String, double[]> o1, Entry<String, double[]> o2) -> Double.compare(o2.getValue()[0],o1.getValue()[0]));
            
            sorted.stream().forEach((val) -> {
                pw_10.write(String.format("%s %f %f %f %f\n", val.getKey(), val.getValue()[0],val.getValue()[1],val.getValue()[2],val.getValue()[3]));
            });
           
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
           // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero_1) {
                    fichero_1.close();
                }
                if (null != fichero_10) {
                    fichero_10.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
}
    
}
