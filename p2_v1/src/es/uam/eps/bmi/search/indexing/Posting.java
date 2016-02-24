package es.uam.eps.bmi.search.indexing;

import java.util.List;

public class Posting {

	private String docId;
	private String term;
	private List<Long> termPositions;

	/**
	 * Crea un posting nuevo para un termino, en un documento con su lista
	 * de posiciones.
	 *
	 * @param docId Id del documento en el que se encuentra el termino.
	 * @param term Termino para el que queremos crear el posting.
	 * @param termPositions Lista de posiciones en las que se encuentra el
	 * termino dentro del documento.
	 */
	public Posting(String docId, String term, List<Long> termPositions) {
		this.docId = docId;
		this.term = term;
		this.termPositions = termPositions;

	}

	/**
	 * Getter del DocId.
	 *
	 * @return
	 */
	public String getDocId() {
		return this.docId;
	}
        
        /**
	 * Getter del term.
	 *
	 * @return
	 */
	public String getTerm() {
		return this.term;
	}

	/**
	 *
	 * @return La frecuencia con la que el termino del posting aparece en el
	 * documento.
	 */
	public int getTermFrequency() {
		return this.termPositions.size();
	}

	/**
	 *
	 * @return Las posiciones del término en el documento del posting.
	 */
	public List<Long> getTermPositions() {
		return this.termPositions;
	}
        
        /**
	 * Añade una posición del término.
	 *
	 * @param pos posicion que añadir.
	 */
	public void addTermPosition(Long pos) {
		this.termPositions.add(pos);
	}

}
