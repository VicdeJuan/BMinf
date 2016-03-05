/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.Posting;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
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
    HashMap<String,Long> dicTermino_Offset = null;//diccionario de offset para cada termino
    double numDoc;
    //HashMap<String,ModuloNombre> dicDocId_ModuloNombre = null;//mantenemos la misma estructura que el indice mejor
     private HashMap diccionarioDocs; //(docId, nombre del documento)
    private HashMap diccionario_docId_modulo; //(docId, modulo)
    //private HashMap diccionarioTerminos_indice; //(termino, offset de bytes en el fichero de indice)
    
    /**
     * Constructor del Reader. 
     * 		Abre el índice para ser leido.
     * 		Carga el diccionario de (término -> offset)
     * @param indice	Path donde se encuentra el índice.
     * @throws FileNotFoundException En caso de no encontrar el fichero del índice.
     * @throws IOException 	En caso de malformación del diccionario o de error de IO.
     * @throws java.lang.ClassNotFoundException     Se ha intentado leer un fichero auxiliar mal formado.
     */
    public BasicReader(String indice) throws FileNotFoundException, IOException, ClassNotFoundException {

        this.accesoIndice = new RandomAccessFile(indice,"r");
        
        ObjectInputStream objectInputStream;
        
        /*objectInputStream = new ObjectInputStream(new FileInputStream(Utils.dicDocId_ModuloNombre_FILE));
	dicDocId_ModuloNombre = (HashMap <String,ModuloNombre>) objectInputStream.readObject();
        
        objectInputStream = new ObjectInputStream(new FileInputStream(Utils.dicTerminoOffset_FILE));
        dicTermino_Offset = (HashMap <String,Long>) objectInputStream.readObject();
	*/
    }

    public BasicReader(String indice,HashMap diccionarioDocs, HashMap diccionario_docId_modulo, HashMap diccionarioTerminos_indice) throws FileNotFoundException {
        this.diccionarioDocs = diccionarioDocs;
        this.diccionario_docId_modulo = diccionario_docId_modulo;
        this.dicTermino_Offset = diccionarioTerminos_indice;
        this.accesoIndice = new RandomAccessFile(indice,"r");
        this.numDoc=diccionarioDocs.keySet().size();
    }
    

    /**
     * Leemos la linea entera del índice en la que se encuentra el término.
     * @param termino	Término del que leer la línea.
     * @return	La linea del índice correspondiente a ese término. 
     * 		En caso de error, se devuelve null
     */
    public String leerLineaDelTermino(String termino) {
        String linea;
        Long offset= dicTermino_Offset.get(termino);
        
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
	public HashMap<String, Long> getDicTermino_Offset() {
		return dicTermino_Offset;
	}

    public HashMap getDiccionarioDocs() {
        return diccionarioDocs;
    }

    public HashMap getDiccionario_docId_modulo() {
        return diccionario_docId_modulo;
    }
        

	List<Posting> getTermPostings(String termino) {
        	List<Posting> toret = new ArrayList();
		String linea= leerLineaDelTermino(termino);
        
        	String[] cadena=linea.split(" ");
        	//0 termino 2 lista de postings k estan separados por comas
	
        	// Al menos tiene que tener el término y la lista de postings.
		// Devolvemos la lista vacía para evitar NULL pointer Exceptions.
		if (cadena.length < 2)
	       		return toret;

		// Si esto no se da, el indice de offsets está mal construido.
		if (!termino.equals(cadena[0])){
			return null;
		}

		List<String> l = new ArrayList<String>(Arrays.asList(cadena));
		//Eliminamos el término.
		String borrado=l.remove(0);
                
                for(String post:l){
                    String[] aux= post.split(",");
                    Posting p=null;


                    List<Long> termPositions = new ArrayList<Long>();

                    String docId = aux[0];
                    String numTerms = aux[1];
                    for (int i=0; i< Integer.parseInt(numTerms); i++){
                        //System.out.println("dentro de stringtoposting: "+s[i+2]);
                        termPositions.add(Long.parseLong(aux[i+2]));
                    }

                    p = new Posting(docId, termPositions);
                    toret.add(p);
                }/*
		l.stream().forEach(
                        (hit) ->  
                            toret.add(getPost(Arrays.asList(hit.split(Utils.InternPostingSeparator)))));
                            */
		return toret;
	}
	

	public double getNumDoc() {
		return numDoc;
	}


	private Posting getPost(List<String> asList) {
		List<Long> positions = new ArrayList<>(asList.size()-2);
		String docId = asList.get(0);
		asList.remove(0);
		asList.remove(1);
		asList.stream().forEach((hit) -> positions.add(Long.parseLong(hit)));
		
		return new Posting(docId,positions);
	}
}
