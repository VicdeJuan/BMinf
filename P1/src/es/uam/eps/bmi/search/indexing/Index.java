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
 * @author e267044
 */
public interface Index {
    
    /**
     * construirá un índice a partir de una colección de documentos de texto. Recibirá como argumentos de entrada las ruta de 
la carpeta en la que se encuentran los documentos a indexar, y l
a ruta de la carpeta en la que almacenar el índice creado, así como un parser de texto que procesará el texto de los documentos 
para su indexación
     *
     * @param inputCollectionPath
     * @param outputIndexPath
     * @param textParser
     */
    void build(String inputCollectionPath, String outputIndexPath, TextParser textParser);

    /** cargará en RAM (parcial o completamente) un índice creado previamente, 
y que se encuentra almacenado en la carpeta cuya ruta se pasa como argumento de entrada.
     *
     * @param indexPath
     */
    void load (String indexPath);

    /** que devuelve la ruta de la carpeta donde está almacenado el índice
     *
     * @return
     */
    String getPath();

    /** que devuelve los identificadores de los documentos indexados.
     *
     * @return
     */
    List<String> getDocIds();


    /** devuelve el documento del identificador dado.
     *
     * @param docId
     * @return
     */
    
TextDocument getDocument(String docId);


    /**devuelve la lista de términos extraídos de los documentos indexados.
     *
     * @return
     */
    
List<String> getTerms();


    /**devuelve los postings de un término dado
     *
     * @param term
     * @return
     */
    
List<Posting> getTermPostings(String term);
}
