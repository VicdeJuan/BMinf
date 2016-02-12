/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author dani
 */
public class HtmlParser implements TextParser {

	
public static void main (String[] args){
	
	String html = "<p>An <a href='http://example.com/'>\n\n<b>example</b></a> link.</p>";
	org.jsoup.nodes.Document doc = Jsoup.parse(html);
	HtmlParser p = new HtmlParser();
	System.out.println(p.parse(html));
}
    @Override
    public String parse(String text) {
	   // Element doc;
	    //doc = parseDoc(text).body();
	    //return doc.text();
           return Jsoup.parse(text).text();
    }
    public org.jsoup.nodes.Document parseDoc(String text) {
        return Jsoup.parse(text) ;    
    }
    
}
