/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.Index;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author dani
 */
public class LuceneSearcher implements Searcher {
    String indexdir;

    /** recibe como argumento de entrada la ruta de la carpeta que contenga
un índice Lucene, y que de forma iterativa pida al usuario consultas a ejecutar por el buscador sobre el índice y
muestre por pantalla los top 5 documentos devueltos por el buscador para cada consulta
     *
     * @param inputCollectionPath
     */
    
    public static void main(String inputCollectionPath){
    
        
        
    }
    @Override
    public void build(Index index) {
        
        
    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        List<ScoredTextDocument> scored=new ArrayList<ScoredTextDocument>();
        Directory dir = null;
        IndexSearcher is=null;
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
        

        IndexReader reader=null;
        try {
            reader = IndexReader.open(FSDirectory.open(new File(this.indexdir)));
        } catch (IOException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error en LuceneSearcher");
            return null;
        }
        
        IndexSearcher searcher = new IndexSearcher(reader);
        
        
        Query q=null;
        try {
            q = new QueryParser(Version.LUCENE_35, "title", analyzer).parse(query);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error en LuceneSearcher");
            return null;
        };
        TopDocs top=null;
        try {
            top=searcher.search(q, null, 100);
        } catch (IOException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LuceneSearcher");
            return null;
        }
        for(int j=0;j<100;j++){
           ScoredTextDocument textdoc =     new ScoredTextDocument ("necesito el path",top.scoreDocs[j].score);
           scored.add(textdoc);
        }
        return scored;
    }
     
    
    /**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   */
    
    
  
    
}
