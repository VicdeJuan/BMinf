/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.Utils;
import static es.uam.eps.bmi.search.Utils.*;
import es.uam.eps.bmi.search.indexing.Posting;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author e267044
 */
public class BasicReader {
    RandomAccessFile accesoIndice; // indice
    HashMap<String,Long> dicOffset = null;//diccionario de offset para cada termino
    double numDoc;


    String dicFile = "cambiarlo";
    
    
    /**
     * Constructor del Reader. 
     * 		Abre el índice para ser leido.
     * 		Carga el diccionario de (término -> offset)
     * @param indice	Path donde se encuentra el índice.
     * @throws FileNotFoundException En caso de no encontrar el fichero del índice.
     * @throws IOException 	En caso de malformación del diccionario o de error de IO.
     */
    public BasicReader(String indice) throws FileNotFoundException, IOException {

        this.accesoIndice = new RandomAccessFile(indice,"r");
	dicOffset = new HashMap<>();
	Utils.loadDic(dicFile,dicOffset,DIC_TYPE.LONG);
    }

    /**
     * Leemos la linea entera del índice en la que se encuentra el término.
     * @param termino	Término del que leer la línea.
     * @return	La linea del índice correspondiente a ese término. 
     * 		En caso de error, se devuelve null
     */
    public String leerLineaDelTermino(String termino) {
        String linea;
        Long offset= dicOffset.get(termino);
        
	    try {
		accesoIndice.seek(offset);
        	linea = accesoIndice.readLine();
	    } catch (IOException ex) {
		Logger.getLogger(BasicReader.class.getName()).log(Level.SEVERE, null, ex);
		linea = null;
	    }
        /*        
        String[] cadena=linea.split(" ");
        //0 termino 1 modulo 2 lista de postings k estan separados por comas
    */
    return linea;
    }
    
    	/**
	 * Getter del diccionario de Offset.
	 * @return 
	 */
	public HashMap<String, Long> getDicOffset() {
		return dicOffset;
	}

	List<Posting> getTermPostings(String termino) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public double getNumDoc() {
		return numDoc;
	}
}
