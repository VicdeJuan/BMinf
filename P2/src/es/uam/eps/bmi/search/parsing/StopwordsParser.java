package es.uam.eps.bmi.search.parsing;

import es.uam.eps.bmi.search.Utils;
import java.util.StringTokenizer;
import java.util.TreeSet;
import org.jsoup.Jsoup;

public class StopwordsParser extends HTMLSimpleParser {
    
    TreeSet<String> stopwordsBag;
    
    public StopwordsParser(){
        stopwordsBag = new TreeSet<>();
        stopwordsBag.add("a");
        stopwordsBag.add("an");
        stopwordsBag.add("and");
        stopwordsBag.add("are");
        stopwordsBag.add("as");
        stopwordsBag.add("at");
        stopwordsBag.add("be");
        stopwordsBag.add("but");
        stopwordsBag.add("by");
        stopwordsBag.add("for");
        stopwordsBag.add("if");
        stopwordsBag.add("in");
        stopwordsBag.add("into");
        stopwordsBag.add("is");
        stopwordsBag.add("it");
        stopwordsBag.add("no");
        stopwordsBag.add("not");
        stopwordsBag.add("of");
        stopwordsBag.add("on");
        stopwordsBag.add("or");
        stopwordsBag.add("such");
        stopwordsBag.add("that");
        stopwordsBag.add("the");
        stopwordsBag.add("their");
        stopwordsBag.add("then");
        stopwordsBag.add("there");
        stopwordsBag.add("these");
        stopwordsBag.add("they");
        stopwordsBag.add("this");
        stopwordsBag.add("to");
        stopwordsBag.add("was");

    }
    
    	/**
	 * Parseo del html utilizando la libreria jsoup y la colección de stopwords 
         *      proporcionada en moodle.
	 *
	 * @param text Texto a parsear.
	 * @return
	 */
	@Override
	public String parse(String text) {
		String toparse = Jsoup.parse(text).text();
                String toret = "",token;
                
                StringTokenizer tokens = new StringTokenizer(toparse, Utils.DefaultSeparators); //PREGUNTAR SI ESTAN BIEN ESTOS SEPARADORES
                    while (tokens.hasMoreTokens()) {
                        token =  tokens.nextToken();
                        if (!stopwordsBag.contains(token))
                            toret += token;
                    }
                
		return toret;
	}

    
}
