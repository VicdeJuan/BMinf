/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.Index;
import java.util.List;

/**
 *
 * @author dani
 */
public class LuceneSearcher implements Searcher {


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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
