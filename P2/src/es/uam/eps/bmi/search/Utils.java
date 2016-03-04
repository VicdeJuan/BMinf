package es.uam.eps.bmi.search;

import es.uam.eps.bmi.search.indexing.Posting;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Utils {

	public static final String STR_CONTENT = "contents";
	public static final String STR_ID = "id";
	public static final String STR_MODIFIED = "path";
	public static final String STR_NAME = "name";
	public static final String DOC_STR_NAME = "name";
        
	public static final String STR_INDEX_SEPARATOR = " ";
        public static final String ESPACIO = " ";
        public static final String COMA = ",";
    
        public static final String dicDocId_ModuloNombre_FILE = ".dicDocId_MN";
        public static final String dicTerminoOffset_FILE = ".dicTermOff";
        
        public static final String InternPostingSeparator = ",";
        public static final String ExternPostingSeparator = " ";

    
    public static double _tf_idf(String docid,List<Posting> termPostings,double numDoc){
        double ndoc;
        double tf;
        double idf;
        
        Posting post = termPostings.get(termPostings.indexOf(docid));
        tf = post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
        
        ndoc = termPostings.size();
        idf = Math.log(numDoc / ndoc) / Math.log(2);
        
        return tf*idf;
    }
    

}
