package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;

public interface Recommender {
	/**
	 * Devuelve la puntuación de un usuario y un item.
	 * @param user
	 * @param item
	 * @return 
	 */
	double rank(int user,int item);
	
	
	
	
}
