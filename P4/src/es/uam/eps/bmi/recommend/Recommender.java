package es.uam.eps.bmi.recommend;

public interface Recommender {
	
	
	/**
	 * Devuelve la puntuación de un usuario y un item.
	 * @param user
	 * @param item
	 * @return 
	 */
	double rank(int user,int item);
	
	
	
	
}
