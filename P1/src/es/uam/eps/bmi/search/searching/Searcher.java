package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.Index;
import java.util.List;

public interface Searcher {

    /** Crea el buscador a partir del índice pasado como argumento de entrada.
     *
     * @param index
     */
    void build(Index index);


    /**Devolverá un ranking(ordenado   por   score decreciente) de documentos
     *  resultantes de ejecutar una consultada dada sobre el índice del buscado.
     * @param query consulta que buscar.
     * @return ranking de los documentos resultantes.
     */
    
List<ScoredTextDocument> search (String query);

}
