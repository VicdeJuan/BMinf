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
 * @author e267044
 */
public interface Searcher {
    void build(Index index);
/* que crea el buscador a partir del índice pasado como argumento de entrada.
*/
List<ScoredTextDocument> search (String query);
 /*devolverá   un 
ranki
ng
(ordenado   por   score 
decreciente) de documentos, resultantes de ejecutar una consultada dada sobre el índice del buscado
*/
}
