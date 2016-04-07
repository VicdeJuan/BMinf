package es.uam.eps.bmi.search;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Integer.sum;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

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
        
        public static int RAM_LIMIT = 400000000;
        
        public static String DefaultSeparators =" ;\n\r\t.,?¿!¡-'";
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

    
    	public static int getSizeOfFile(String file) {
		String line;
		int size = 0;
		TreeSet<String> bag = new TreeSet<>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				String[] s = line.split(" ");
				bag.add(s[0]);
				bag.addAll(Arrays.asList(s).subList(2, s.length));
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return bag.size();
	}
	
	public static double coseno(double[] v1,double[] v2){
		
		if (v1.length != v2.length)
			return Double.NaN;
		double m1=0,m2=0,sum=0;
		for(int i = 0; i < v1.length;i++){
			m1 += Math.pow(v1[i],2);
			m2 += Math.pow(v2[i],2);
			sum += v1[i]*v2[i];			
		}
		return sum/(Math.sqrt(m1)+Math.sqrt(m2));
	}

}

    

