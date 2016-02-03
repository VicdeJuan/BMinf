/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import java.util.List;

/**
 *
 * @author e267044
 */
public interface Index {
    void build(String inputCollectionPath, String outputIndexPath, TextParser textParser);
/*, que 
construirá
un 
índice a partir de una colección de documentos de texto. Recibirá como argumentos de entrada las ruta de 
la carpeta en la que se encuentran los documentos a indexar, y l
a ruta de la carpeta en la que almacenar
el 
índice creado, así como un 
parser
de texto que procesará el texto de los documentos 
para su indexación
.
*/
void load (String indexPath);
/*, que cargará en 
RAM (
parcial o completamente
)
un índice creado previamente, 
y qu
e se encuentra almacenado en la carpeta cuya ruta se pasa como argumento de entrada.
o*/
String getPath();
/* que devuelve la ruta de la carpeta donde está almacenado el índice.*/

List<String> getDocIds();
/*, que
devuelve los identificadores de los documentos indexados.
*/
TextDocument getDocument(String docId);
/* que devuelve el documento del identificador dado.
*/
List<String> getTerms();
/* que
devuelve la lista de términos extraídos de los documentos indexados.
*/
List<Posting> getTermPostings(String term);
/* que devuelve los 
postings
de un término dado*/
}
