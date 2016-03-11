package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.StemParser;
import es.uam.eps.bmi.search.parsing.TextParser;

public class StemIndex extends BasicIndex{
	
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        super.build(inputCollectionPath, outputIndexPath, new StemParser());
    }

}