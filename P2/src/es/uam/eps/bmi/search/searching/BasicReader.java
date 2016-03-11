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



public class BasicReader {
    RandomAccessFile accesoIndice; // indice
    
    double numDoc;
    //HashMap<String,ModuloNombre> dicDocId_ModuloNombre = null;//mantenemos la misma estructura que el indice mejor
     
    private HashMap<String,ModuloNombre> diccionarioDocs_NM; //(docId, (nombre del documento,modulo))

    public HashMap<String, ModuloNombre> getDiccionarioDocs_NM() {
        return diccionarioDocs_NM;
    }

    public HashMap<String, Long> getDiccionarioTerminos_indice() {
        return diccionarioTerminos_indice;
    }
    private HashMap<String,Long> diccionarioTerminos_indice; //(termino, offset de bytes en el fichero de indice)
    
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
        
        objectInputStream = new ObjectInputStream(new FileInputStream(Utils.dicDocId_ModuloNombre_FILE));
	diccionarioDocs_NM = (HashMap <String,ModuloNombre>) objectInputStream.readObject();
        
        objectInputStream = new ObjectInputStream(new FileInputStream(Utils.dicTerminoOffset_FILE));
        diccionarioTerminos_indice = (HashMap <String,Long>) objectInputStream.readObject();
        
        this.numDoc=diccionarioDocs_NM.keySet().size();
	
    }

    public BasicReader(String indice,HashMap<String, ModuloNombre> diccionarioDocs_NM, HashMap<String, Long> diccionarioTerminos_indice) throws FileNotFoundException {
        this.diccionarioDocs_NM = diccionarioDocs_NM;
        this.diccionarioTerminos_indice = diccionarioTerminos_indice;
         this.accesoIndice = new RandomAccessFile(indice,"r");
         this.numDoc= diccionarioDocs_NM.entrySet().size();
    }
     

    /**
     * Leemos la linea entera del índice en la que se encuentra el término.
     * @param termino	Término del que leer la línea.
     * @return	La linea del índice correspondiente a ese término. 
     * 		En caso de error, se devuelve null
     */
    public String leerLineaDelTermino(String termino) {
        String linea;
        Long offset= diccionarioTerminos_indice.get(termino);
        if (offset == null)
            return null;
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
         * Devuelve la lista de postings de un termino.
         * @param termino   termino del que devolver su lista de postings.
         * @return  Lista de postings del termino.
         */
	List<Posting> getTermPostings(String termino) {
        	List<Posting> toret = new ArrayList();
		String linea= leerLineaDelTermino(termino);
                if (linea == null)
                    return toret;
                
        	String[] cadena=linea.split(Utils.ExternPostingSeparator);
        	//0 termino 2 lista de postings k estan separados por comas
	
        	// Al menos tiene que tener el término y la lista de postings.
		// Devolvemos la lista vacía para evitar NULL pointer Exceptions.
		if (cadena.length < 2)
	       		return toret;

		// Si esto no se da, el indice de offsets está mal construido.
		if (!termino.equals(cadena[0])){
			return null;
		}

		ArrayList<String> l = new ArrayList<>(Arrays.asList(cadena));
		//Eliminamos el término.
		l.remove(0);
                
                for(String post:l){
                    String[] aux= post.split(",");
                    Posting p=null;


                    ArrayList<Long> termPositions = new ArrayList<>();

                    String docId = aux[0];
                    String numTerms = aux[1];
                    for (int i=0; i< Integer.parseInt(numTerms); i++){
                        //System.out.println("dentro de stringtoposting: "+s[i+2]);
                        termPositions.add(Long.parseLong(aux[i+2]));
                    }

                    p = new Posting(docId, termPositions);
                    toret.add(p);
                }
		return toret;
	}
	

        /**
         * 
         * @return El numero de documentos del indice.
         */
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