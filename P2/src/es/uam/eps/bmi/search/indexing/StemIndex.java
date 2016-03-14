package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.StemParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.searching.BasicReader;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StemIndex extends BasicIndex{
	
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        super.build(inputCollectionPath, outputIndexPath, new StemParser());
    }



}