/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author dani
 */
public class HTMLSimpleParser implements TextParser {


	public String PorterStemming(String text){
		HashMap<String,String> stem = new HashMap<>();
		stem.put("sses","ss");
		stem.put("as","a");
		stem.put("es","e");
		stem.put("is","i");
		stem.put("os","o");
		stem.put("us","u");
		stem.put("ied","i");
		stem.put("ies","ie");

                String toret = text;
		for(Map.Entry<String, String> k : stem.entrySet())
                    toret = toret.replaceAll(k.getKey(), k.getValue());

                return toret;
	}

    @Override
    public String parse(String text) {
	   // Element doc;
	    //doc = parseDoc(text).body();
	    //return doc.text();
           //String aux = text.split("DOCTYPE")[1];
           String toret =  Jsoup.parse(text).text();
           //toret = toret.replaceAll(",|\\.|'s", "");
           return toret;
    }
    public org.jsoup.nodes.Document parseDoc(String text) {
        return Jsoup.parse(text) ;
    }

}
