/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.parsing.TextParser;
import java.util.List;

/**
 *
 * @author dani
 */
public class LuceneIndex implements Index{
/* 
    dos argumentos de entrada: la ruta de la carpeta que contiene la colección de documentos con
los que crear el índice, y la ruta de la carpeta en la que almacenar el índice creado.*/

    /**
     *
     * @param inputCollectionPath
     * @param outputCollectionPath
     */
    
    public static void main(String inputCollectionPath,String outputCollectionPath){
    
    }
    
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void load(String indexPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getDocIds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TextDocument getDocument(String docId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getTerms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Posting> getTermPostings(String term) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
