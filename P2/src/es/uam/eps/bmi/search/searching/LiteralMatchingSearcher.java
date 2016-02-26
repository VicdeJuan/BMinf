/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.QueryParser;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author dani
 */
public class LiteralMatchingSearcher implements Searcher{

  
    BasicReader indice;
    private String indexdir;
    private int TOP=5;
    
    @Override
    public void build(Index index) {
        this.indexdir = index.getPath();
        BasicIndex basicIndex = (BasicIndex) index;
	
    }

    @Override
    //Esta la normal, no literal
    public List<ScoredTextDocument> search(String query) {
         List<ScoredTextDocument> toret = null;
         BinaryHeap heap;
        String[] querys;
        TreeMap<String,Double> doctf_id=new TreeMap();
        
        // hacer un query parser 
        querys = new QueryParser().parses(query);
        //cada qaux es un termino
        for(String qaux: querys){
             List<Posting> termPostings = indice.getTermPostings(qaux);
             for(Posting post:termPostings){
                 
                /* 
                double tf_idf=tf_idf(qaux,post.getDocId());
                if(!doctf_id.containsKey(qaux)){
                    doctf_id.put(post.getDocId(),tf_idf );
                }
                double old=doctf_id.get(qaux);
                old +=tf_idf;
                doctf_id.put(qaux, old);
                 */
                       
             }
        }
        for(String docid:doctf_id.keySet()){
            double old=doctf_id.get(docid);
            //Falta dividir old por el modulo del documento       
            doctf_id.put(docid,old);
        }
        int k=1;
        
        for(String doc:doctf_id.keySet()){
            if(k<=TOP){
            ScoredTextDocument aux= new ScoredTextDocument(doc,doctf_id.get(doc));
            toret.add(aux);
            k++;
            }
            else{
            break;
            }
        }
        
      
        
        return toret;
    }
   
    /**
     * Computa el producto de tf*id de un término.
     * @param termino
     * @return 
     */
    public double tf_idf(String termino ) {
        
	// ESTA FUNCIÓN ESTÁ MAL HECHA.
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
    
        public double tf_idf(String termino, String docid ) {
         
	
	double freq = 0;
	double ndoc = 0;
	double tf = 0;
	double idf = 0;
	List<Posting> termPostings = indice.getTermPostings(termino);
	for (Posting post : termPostings){
		// Cada vuelta es en un documento distinto.
            if(post.getDocId().equals(docid)){
                
                freq = post.getTermFrequency();
		
		tf = post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
            }
            ndoc++;
            
            
		
	}
	// val 2 = tf
	//tf = freq == 0 ? 1 : 1+Math.log(freq)/Math.log(2);
	// val 3 = idf = log(nº doc/nºdocs con ese termino)
	idf = Math.log(indice.getNumDoc() / ndoc) / Math.log(2);
		
    return tf*idf;
        }
        
        
    
    
}
