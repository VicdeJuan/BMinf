package es.uam.eps.bmi.search.parsing;

import es.uam.eps.bmi.search.parsing.stemming.PorterStemmer;
import es.uam.eps.bmi.search.parsing.stemming.Stemmer;

public class StemParser extends HTMLSimpleParser{
    
	Stemmer st;

	public StemParser(){
		st = new PorterStemmer();		
	}
	
	@Override
	public String parse (String text){
		return st.stem(text);
	}
}
