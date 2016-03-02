/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.QueryParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 *
 * @author e267044
 */
public class TFIDFSearcher implements Searcher{
    
    BasicReader indice;
    private String indexdir;
    private final static int TOP=5;
    
    
    	public static void main(String[] args) throws IOException {
		
		String inputCollectionPath = "indice.txt";
		String outputCollectionPath = "querys.txt";
                
                BasicIndex basicIdx = new BasicIndex();
                

		if (basicIdx.getReader() != null || true ) {
			TFIDFSearcher tfSearch = new TFIDFSearcher();
			tfSearch.build(basicIdx);
			//ahora leemos de teclado las querys
			System.out.println("Introducir las palabras de la búsqueda:");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String query = br.readLine();
                        String prueba="a b";
                        List<ScoredTextDocument> search = tfSearch.search(prueba);

			List<ScoredTextDocument> resul = tfSearch.search(query);
			if (resul != null && resul.size() > 0) {
				for (int i = 0; i < TOP; i++) {
					System.out.println(resul.get(i).getDocId());
				}

			} else {
				System.out.println("Consulta vacia");
			}

		}
                
                
	}
    
    @Override
    public void build(Index index) {
        //this.indexdir = index.getPath();
        //this.indice= ((BasicIndex)index).getReader();
        String indice="indice.txt";
        try {
            this.indice= new BasicReader(indice);
        } catch (IOException ex) {
            Logger.getLogger(TFIDFSearcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TFIDFSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
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
                double tf_idf=tf_idf(qaux,post.getDocId());
                if(!doctf_id.containsKey(qaux)){
                    doctf_id.put(post.getDocId(),tf_idf );
                }
                double old=doctf_id.get(qaux);
                old +=tf_idf;
                doctf_id.put(qaux, old);
                       
             }
        }
        for(String docid:doctf_id.keySet()){
            double old=doctf_id.get(docid);
            //Falta dividir old por el modulo del documento    
            //FALTAAAAAAAAAAAAAAAAAAAAAAAAAAAAa
            //FALTAAAAAAAAAAAAAAAAAAAA que no se olvide
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
       public double tf_idf(String termino, String docid) {

        
        double ndoc;
        double tf;
        double idf;
        List<Posting> termPostings = indice.getTermPostings(termino);
        Posting post = termPostings.get(termPostings.indexOf(docid));

        tf = post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
        ndoc = termPostings.size();

        
        // val 2 = tf
        //tf = freq == 0 ? 1 : 1+Math.log(freq)/Math.log(2);
        // val 3 = idf = log(nº doc/nºdocs con ese termino)
        idf = Math.log(indice.getNumDoc() / ndoc) / Math.log(2);

        return tf * idf;
    }
    
        
        
    
}
