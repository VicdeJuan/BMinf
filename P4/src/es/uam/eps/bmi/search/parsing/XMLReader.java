package es.uam.eps.bmi.search.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReader {
	Element doc;
	private static ArrayList<String> rolev;
	
	public String getTextValue(String tag) {
		String value ="";
		NodeList nl;
		nl = doc.getElementsByTagName(tag);
		if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
			value = nl.item(0).getFirstChild().getNodeValue();
		}
		value+="/";
		return value;
	}

	public XMLReader(String xml){
		if (!readXML(xml)){
			System.err.println("No se ha podido cargar el XML");	
			System.exit(-1);
		}
	}
	private boolean readXML(String xml) {
		rolev = new ArrayList<>();
		Document dom;
		// Make an  instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use the factory to take an instance of the document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
			// XML file
			dom = db.parse(xml);

			doc = dom.getDocumentElement();

			return true;

		} catch (ParserConfigurationException pce) {
			System.out.println(pce.getMessage());
		} catch (SAXException | IOException ex) {
			Logger.getLogger("").log(Level.SEVERE, null, ex);
		}

		return false;
	}	
}
