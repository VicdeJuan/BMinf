package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.StemParser;
import es.uam.eps.bmi.search.parsing.XMLReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IndexBuilder {

	static String collectionFolder;
	static String indexFolder;
	static HashMap<String, Index> indices;
	
	private static ArrayList<String> rolev;


	private static void checkFolders(){
		File f = new File(collectionFolder);
		if (!(f.exists() && f.isDirectory())){
			System.out.println("La configuración no es correcta.\n\t" + collectionFolder + " no es un directorio.");
			System.exit(-1);
		}
		
		f = new File(indexFolder);
		if (!f.exists()){
			try{
				f.mkdir();
			}catch(Exception e){
				System.out.println(indexFolder+" no existe y no se ha podido crear");
				System.exit(-1);
			}
		}
	}

	public static void main(String[] argv) {

		// Recibir como argumentos:
		XMLReader xmlReader = new XMLReader("index-settings.xml");
		collectionFolder = xmlReader.getTextValue(Utils.XMLTAG_COLLECTIONFOLDER);
		indexFolder = xmlReader.getTextValue(Utils.XMLTAG_INDEXFOLDER);
		checkFolders();

		System.out.println("La configuración es correcta.");
		
		// Procesado en sí.
		indexFolder += "/";
		indices = new HashMap<>();
		indices.put("stem", (Index) new StemIndex());
		indices.put("stopword", (Index) new StopwordIndex());
		indices.put("basic", (Index) new BasicIndex());

		for (Map.Entry<String, Index> e : indices.entrySet()) {
			System.out.println("" + e.getKey()+":");

			File dir = new File(indexFolder + e.getKey());
			boolean c = dir.mkdir();

			// Stop y Stem ignoran en tercer argumento.
			e.getValue().build(collectionFolder , indexFolder + e.getKey()  , new HTMLSimpleParser());
			System.out.println("Creado " + e.getKey() + "index"); 
		}
	}
}
