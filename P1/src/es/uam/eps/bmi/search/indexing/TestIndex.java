package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;



public class TestIndex {

  

    public TestIndex() {
    }
    
    /**
     * Metodo autxiliar para el test de un index. 
     * Crea el indice a partir de los argumentos dados.
     * @param input     Ruta de los documentos que indexar.
     * @param collectionPath    Ruta en la que almacenar el indice.
     * @param parser    Parser con el que procesar el texto de los documentos.
     * @param outputFile Fichero en el que almacenar las frecuencias de los 
     *  terminos de la coleccion de documentos.
     */
    private static void _test_create(String input, String collectionPath, TextParser parser,String outputFile){
        _test(outputFile,new LuceneIndex(input, collectionPath, parser));
    }
    
    /**
     * Metodo auxiliar para el test de un index.
     * Carga el indice a partir de los argumentos dados.
     * @param collectionPath Ruta en la que se encuentra el indice.
     * @param outputFile Fichero en el que almacenar las frecuencias de los 
     *  terminos de la coleccion de documentos. 
     */
    private static void _test_load(String collectionPath,String outputFile){
        _test(outputFile,new LuceneIndex(collectionPath));
    }
    
    /**
     * Metodo base para el test del index.
     * @param outputFile    Fichero en el que almacenar el resultado. El fichero
     *  se compone de 5 columnas: termino frecuencia numdocs tf idf
     * @param Idx       Indice para el test.
     */
    private static void _test(String outputFile,Index Idx){
        
        LuceneIndex LucIdx = (LuceneIndex) Idx;
        FileWriter fichero = null;
        
        try {
            fichero = new FileWriter(outputFile);
        
        } catch (IOException ex) {
            Logger.getLogger(TestIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        final PrintWriter pw = new PrintWriter(fichero);    
        
        try {
            // term -> (freq, ndoc, tf,idf)
	    final LinkedHashMap <String,double[]> freqHash = new LinkedHashMap<>();
            
                LucIdx.getTerms().stream().forEach((term) -> {
                double[] val = new double[] {0,0,0,0};
                List<Posting> termPostings = LucIdx.getTermPostings(term);
                termPostings.stream().forEach((post) -> {
                    val[0] += post.getTermFrequency();
                    val[1]++;
                    val[2] += post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency())/Math.log(2);
             });
                // val 2 = tf
                val[2] = val[2]/LucIdx.getNumDoc();
                // val 3 = idf
                val[3] = Math.log(LucIdx.getNumDoc()/val[1]);
                freqHash.put(term,val);
             });
            
                
             /** La penalizacion al ordenar es despreciable frente al tiempo 
              *      de generacion del indice
              */
            List<Entry<String,double[]>> sorted = new LinkedList <>(freqHash.entrySet());
            Collections.sort(sorted, (Entry<String, double[]> o1, Entry<String, double[]> o2) -> Double.compare(o2.getValue()[0],o1.getValue()[0]));
            
            sorted.stream().forEach((val) -> {
                pw.write(String.format("%s %f %f %f %f\n", val.getKey(), val.getValue()[0],val.getValue()[1],val.getValue()[2],val.getValue()[3]));
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally{
            
	    if (null != fichero) 
                try {
                    fichero.close();
                } catch (IOException ex) {
                    Logger.getLogger(TestIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * @param args: no se utilizan.
     */
    public static void main (String[] args){
        
        String file = "1";
	String inputCollectionPath = "src/es/uam/eps/bmi/clueweb-"+file+"K";
        String outputCollectionPath = "outputCollection_"+file;
        String outputFile ="src/es/uam/eps/bmi/frecuencias"+file+"K.txt";
        	
        boolean regen = true;
	
        if (regen) 
            _test_create(inputCollectionPath, outputCollectionPath, new HTMLSimpleParser(), outputFile);
        else
            _test_load(outputCollectionPath,outputFile);	
    }
    
}
