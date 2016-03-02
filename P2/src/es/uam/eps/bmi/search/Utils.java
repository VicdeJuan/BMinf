package es.uam.eps.bmi.search;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Utils {

	public static final String STR_CONTENT = "contents";
	public static final String STR_ID = "id";
	public static final String STR_MODIFIED = "path";
	public static final String STR_NAME = "name";
	public static final String DOC_STR_NAME = "name";
	
	public static final String STR_DIC_OFFSET_SEPARATOR = " ";
	public static final String STR_POSTING_SEPARATOR = ",";
	public static final String STR_INDEX_SEPARATOR = " ";
        public static final String ESPACIO = " ";
        public static final String COMA = ",";
    
        public static String dicDocId_ModuloNombre_FILE = ".dicDocId_MN";
        public static String dicTerminoOffset_FILE = ".dicTermOff";
    
	
    static public enum DIC_TYPE {LONG,STR};
    /**
     * Crea un diccionario a partir de un fichero, tomando
     * 		primera columna como clave
     * 		segunda columna como valor
     * 	
     * @param DicFile 	Ruta del fichero que cargar.
     * @param tofill	Diccionario para rellenar.
     * @param type	Clase del valor, un valor de DIC_TYPE_*.
     * 				Por defecto, se almacena como String lo que se haya leido.
     * @throws IOException En caso de fichero mal formado o de error de IO.
     */
    static public void loadDic(String DicFile,HashMap tofill, DIC_TYPE type ) throws IOException {
	    for (String line : Files.readAllLines(Paths.get(DicFile))){
			String[] a = line.split(STR_DIC_OFFSET_SEPARATOR);
			if (a.length < 2) {
				throw new IOException("Fichero "+DicFile+" mal formado");
			}
			if(type == DIC_TYPE.LONG)
				tofill.put(a[0],Long.parseLong(a[1]));
			else
				tofill.put(a[0], a[1]);
	    }
    }
    
}
