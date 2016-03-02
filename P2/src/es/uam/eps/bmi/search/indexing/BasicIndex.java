/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.searching.BasicReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author parra
 */
public class BasicIndex implements Index {
    private String path;
    private ArrayList<ArrayList<Posting>> indice1;
    private HashMap diccionarioTerminos; //(termino, linea del txt del indice)
    private HashMap diccionarioDocs; //(nombre del documento, id guardado en indice)
    private BasicReader reader;
    
    public BasicIndex(String path, ArrayList<ArrayList<Posting>> indice1, ArrayList<ArrayList<Posting>> indice2, HashMap diccionario) {
        this.path = path;
        this.indice1 = indice1;
        this.diccionarioTerminos = diccionario;
    }

    public BasicIndex() {
        
    }
    
 
    
    

    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        
        
        
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

    public BasicReader getReader() {
        return reader;
    }
    
}
