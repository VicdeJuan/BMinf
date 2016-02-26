/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

import org.jsoup.Jsoup;

/**
 *
 * @author dani
 */
public class QueryParser implements TextParser {


    public QueryParser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String parse(String text) {
        String aux = Jsoup.parse(text).text();
        
        return aux;
    }

    /**
     *
     * @param text
     * @return
     */
    public String[] parses(String text) {
        String aux = Jsoup.parse(text).text();
        String[] split = aux.split(" ");
        return split;
    }
    
    
}
