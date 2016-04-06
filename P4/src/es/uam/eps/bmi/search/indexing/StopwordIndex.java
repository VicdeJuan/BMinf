package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.StopwordsParser;
import es.uam.eps.bmi.search.parsing.TextParser;

public class StopwordIndex extends BasicIndex {
    
    
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        super.build(inputCollectionPath, outputIndexPath, new StopwordsParser());
    }

    
    
}
