package es.uam.eps.bmi.search;

public class ScoredTextDocument implements java.lang.Comparable {

	private String docId;
	private double score;

        /**
         * Constructor de la clase, dados el id y su puntuacion.
         * @param docId Id del documento.
         * @param score Puntuacion.
         */
	public ScoredTextDocument(String docId, double score) {
		this.docId = docId;
		this.score = score;
	}

	/**
	 *
	 * @return el identificador del documento asociado al resultado
	 */
	public String getDocId() {
		return this.docId;
	}

	/**
	 *
	 * @return el score asociado al resultado.
	 */
	public double getScore() {
		return this.score;
	}

	@Override
	public int compareTo(Object o) {
		int aux = (int) (this.score - ((ScoredTextDocument) o).getScore());

		return aux;
	}
}
