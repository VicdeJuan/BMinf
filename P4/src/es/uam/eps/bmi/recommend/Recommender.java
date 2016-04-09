package es.uam.eps.bmi.recommend;

import java.util.List;

public interface Recommender {
	
	
	/**
	 * Devuelve la puntuación de un usuario y un item.
	 * @param user
	 * @param item
	 * @return 
	 */
	double rank(int user,int item);
	
	/**
	 *  Recomienda para un usuario tantos elementos como size.
	 * @param user	Usuario para el que recomendar
	 * @param size	Número de elementos que recomendar.
	 * @return 
	 */
	List<UserValue> recommend(int user,int size);
	
	
}
