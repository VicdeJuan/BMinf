/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search;

/**
 *
 * @author e267044
 */
public class ScoredTextDocument implements java.lang.Comparable {

	private String docId;
	private double score;

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
