package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.searching.BasicReader;
import java.util.List;

public interface Index {

	/**
	 * Construye un índice a partir de una colección de documentos de texto.
	 *
	 * @param inputCollectionPath ruta de la carpeta en la que se encuentran
	 * los documentos a indexar
	 * @param outputIndexPath ruta de la carpeta en la que almacenar el
	 * índice creado,
	 * @param textParser parser de texto que procesará el texto de los
	 * documentos para su indexación
	 */
	void build(String inputCollectionPath, String outputIndexPath, TextParser textParser);

	/**
	 * Cargará en RAM (parcial o completamente) un índice creado
	 * previamente, y que se encuentra almacenado en la carpeta cuya ruta se
	 * pasa como argumento de entrada.
	 *
	 * @param indexPath
	 */
	void load(String indexPath);

        
        
        
        
        
	/**
	 * Devuelve la ruta de la carpeta donde está almacenado el índice
	 *
	 * @return
	 */
	String getPath();

	/**
	 * Devuelve los identificadores de los documentos indexados.
	 *
	 * @return
	 */
	List<String> getDocIds();

	/**
	 * Devuelve el documento del identificador dado.
	 *
	 * @param docId
	 * @return
	 */
	TextDocument getDocument(String docId);

	/**
	 * Devuelve la lista de términos extraídos de los documentos indexados.
	 *
	 * @return
	 */
	List<String> getTerms();

	/**
	 * Devuelve los postings de un término dado
	 *
	 * @param term
	 * @return
	 */
	List<Posting> getTermPostings(String term);

	/**
	 * Carga un lector del índice.
	 */
	public void loadReader();

	/**
	 * Devuelve el lector del índice.
	 * @return 
	 */
	public BasicReader getReader();
}
