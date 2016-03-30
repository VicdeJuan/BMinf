package es.uam.eps.bmi.search.parsing;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.stemming.PorterStemmer;
import es.uam.eps.bmi.search.parsing.stemming.Stemmer;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;

public class StemParser extends HTMLSimpleParser{
    
	Stemmer st;

	public StemParser(){
		st = new PorterStemmer();		
	}
	
	@Override
	public String parse (String text){
		String tostem = Jsoup.parse(text).text();
		String token,toret="";
                StringTokenizer tokens = new StringTokenizer(tostem, Utils.DefaultSeparators); 
                    while (tokens.hasMoreTokens()) {
                        token =  tokens.nextToken();
			toret += st.stem(token) + " ";
                    }
                
		return toret;
	}
}
