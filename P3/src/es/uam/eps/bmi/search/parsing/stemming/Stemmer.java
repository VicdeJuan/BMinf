package es.uam.eps.bmi.search.parsing.stemming;

public interface Stemmer {
	
	/**
	 * Devuelve la cadena después del proceso de stemming.
	 * @param str	cadena a procesar.
	 * @return 
	 */
	public String stem (String str);
}
