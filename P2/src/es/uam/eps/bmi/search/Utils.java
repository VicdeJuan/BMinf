package es.uam.eps.bmi.search;

import es.uam.eps.bmi.search.indexing.Posting;
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
        
        public static int RAM_LIMIT = 500000000;
        
        public static String DefaultSeparators =" ;\n\r\t.,";
	/*public static String collection_folder=  "pruebas";
	public static String collection_folder_1K=  "pruebas/clueweb-1K";
	public static String collection_folder_10K=  "pruebas/clueweb-10K";
	public static String collection_folder_100K=  "pruebas/clueweb-100K";
	*/
	
	public static String index_folder = "indices";
	public static String index_file = "idx.txt";
	
	public static final String STR_DEFAULT_ZIPNAME = "docs.zip";
	
	public final static String XMLTAG_INDEXFOLDER= "index-folder";
	public final static String XMLTAG_COLLECTIONFOLDER = "collection-folder";

	/**
	 * Cálculo general del tf_idf(t,d).
	 * @param termino	Término t para calcular.
	 * @param docid		Documento d para calcular.
	 * @param termPostings	Lista de postings del término.
	 * @param numDoc	Número total de documentos.
	 * @return 
	 */
    public static double tf_idf(String termino, String docid, List<Posting> termPostings, double numDoc) {

        double freq = 0;
        double ndoc = 0;
        double tf = 0;
        double idf = 0;
        for (Posting post : termPostings) {
            // Cada vuelta es en un documento distinto.
            if (post.getDocId().equals(docid)) {

                freq = post.getTermFrequency();

                tf = post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
            }
            ndoc++;

        }
        // val 2 = tf
        //tf = freq == 0 ? 1 : 1+Math.log(freq)/Math.log(2);
        // val 3 = idf = log(nº doc/nºdocs con ese termino)
        idf = Math.log(numDoc / ndoc) / Math.log(2);

        return tf * idf;
    }

}

    

