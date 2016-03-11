package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.StemParser;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IndexBuilder {
	
	static String collection_folder;
	static String index_folder;
	static HashMap<String,Index> indices;
	public static void main(String[] argv){

		// Recibir como argumentos:
		collection_folder = Utils.collection_folder; 
		index_folder = Utils.index_folder;
		
		// Procesado en sí.
		index_folder +="/";
		indices = new HashMap<>();
		indices.put("stem", (Index) new StemIndex());
		indices.put("stopword", (Index) new StopwordIndex());
		indices.put("basic",(Index) new BasicIndex());
		
		
		for (Map.Entry<String, Index> e : indices.entrySet()){
			System.out.print(index_folder + e.getKey());

			File dir = new File(index_folder+e.getKey());
	    		boolean c = dir.mkdir();
			
			// Stop y Stem ignoran en tercer argumento.
			e.getValue().build(collection_folder, index_folder+e.getKey(), new HTMLSimpleParser());
			System.out.println("Creado " + e.getKey() +"index");
		}
	}
}
