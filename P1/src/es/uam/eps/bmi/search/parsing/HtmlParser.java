/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;

/**
 *
 * @author dani
 */
public class HtmlParser implements TextParser {

    @Override
    public String parse(String text) {
 return null;
    }
    public org.jsoup.nodes.Document parseDoc(String text) {
        return Jsoup.parse(text) ;    
    }
    
}
