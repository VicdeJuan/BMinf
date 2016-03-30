package es.uam.eps.bmi.search.parsing;

import org.jsoup.Jsoup;

public class HTMLSimpleParser implements TextParser {

	/**
	 * Parseo del html utilizando la libreria jsoup.
	 *
	 * @param text Texto a parsear.
	 * @return
	 */
	@Override
	public String parse(String text) {
		String toret = Jsoup.parse(text).text();
		return toret;
	}

}
