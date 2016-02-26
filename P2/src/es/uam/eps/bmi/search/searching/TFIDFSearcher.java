/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 *
 * @author e267044
 */
public class TFIDFSearcher implements Searcher{
    
    BasicReader indice;

    @Override
    public void build(Index index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    /**
     * Computa el producto de tf*id de un término.
     * @param termino
     * @return 
     */
    public double tf_idf(String termino ) {
        
	// ESTA FUNCIÓN ESTÁ MAL HECHA.
        String linea= indice.leerLineaDelTermino(termino);
        
        String[] cadena=linea.split(" ");
        //0 termino 1 modulo 2 lista de postings k estan separados por comas
	
        // Al menos tiene que tener el término y la lista de postings.
	if (cadena.length < 2)
	       return Double.NaN;
	
	double freq = 0;
	double ndoc = 0;
	double tf = 0;
	double idf = 0;
	List<Posting> termPostings = indice.getTermPostings(termino);
	for (Posting post : termPostings){
		// Cada vuelta es en un documento distinto.
		freq += post.getTermFrequency();
		ndoc++;
		tf += post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
	}
	// val 2 = tf
	tf = freq == 0 ? 1 : 1+Math.log(freq)/Math.log(2);
	// val 3 = idf
	idf = Math.log(indice.getNumDoc() / ndoc);
		
    return tf*idf;
    }
    
}
