package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSearcher {

    
    public static void _evaluate_results(String outputFile,ArrayList<ArrayList<String>> resultados,int max,String compareToFile) throws IOException{
        
            final PrintWriter pwout = new PrintWriter(new FileWriter(outputFile));
            BufferedReader expected_file = new BufferedReader(new FileReader(compareToFile));
            Integer[] encontrados = new Integer[resultados.size()+1];
            for (int x = 0; x < encontrados.length; x++) {
                encontrados[x] = 0;
            }
            int j = 0;
            String cadena;
            while ((cadena = expected_file.readLine()) != null) {

                String[] split = cadena.split("\t");
                List<String> keys = new ArrayList<>(Arrays.asList(split));
                for (int l = 0; l < resultados.get(j).size(); l++) {

                    //vemos si cada elemento de la query esta en relevance
                   
                    for (String key : keys)                     
                        if (key.equals(resultados.get(j).get(l))) {
                            encontrados[j]++;    
                            break;
                        }
                    
                    
                }
                pwout.println("Query: " + (j + 1) + "\t" + encontrados[j]*1.0/max);

                j++;
            }
            pwout.close();
        }
    
    /**
     * Construye el fichero oportuno a partir de las busquedas segun 
     *      los argumentos aportados
     * @param outputFile Fichero en el que almacenar la informacion
     * @param LucIdx     Indice de Lucene.
     * @param queryFile  Fichero con las consultas.
     * @param max        Numero maximo de resultados.
     * @return  La lista de los documentos que corresponden a la query.
     * @throws IOException 
     */
    private static ArrayList<ArrayList<String>> _build_results(String outputFile,LuceneIndex LucIdx,String queryFile, int max) throws IOException{
     
        final PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
        ArrayList<ArrayList<String>> resultados = new ArrayList<>();
        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            
            
	    int indice=1;
	    
            pw.println(" querys Top " + max);
            
            
            for (String query : Files.readAllLines(Paths.get(queryFile))) {
                List<ScoredTextDocument> resul = lucSearch.search(query.substring(query.indexOf(":")+1));
                pw.println(query + " -- " + (indice++) + ":");

                if (resul != null && resul.size() > 0) {
                    ArrayList<String> aux = new ArrayList<>();
                    String toadd;
                    for (int i = 0; i < Math.min(max,resul.size()) ; i++) {
                        toadd = resul.get(i).getDocId().substring(0,resul.get(i).getDocId().lastIndexOf("."));
                        aux.add(toadd);
                        pw.write(toadd+"\t");
                    }
                    resultados.add(aux);
                } else {
                    ArrayList<String> aux = new ArrayList<>();
                    aux.add("Query Vacia");
                    resultados.add(aux);
                    pw.write("Query vacia\t");
                }
		pw.write("\n");
            }

            pw.close();            
            }
        return resultados;
    }
    
    /**
     * Metodo main del test.
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        
        //Definicion de variables a utiliar
        ArrayList<String> ks = new ArrayList<>();
	ks.add("1");
	ks.add("10");
	ks.add("100");
	
	ArrayList<Integer> maxs = new ArrayList<>();
	maxs.add(5);
	maxs.add(10);
	maxs.add(100);
	
	for (String k : ks){
	System.out.print(k + "::");
        String indexPath = "outputCollection_"+k;
        LuceneIndex LucIdx = new LuceneIndex(indexPath);
	
	for (int max : maxs) {
		System.out.println("\t" + max);
		String outputFile_build = "Querys"+k+"K_"+max+".txt";
		String outputFile_evaluate = "Comparacion"+max+"-"+k+"K.txt";
		String queryFile = "src/es/uam/eps/bmi/clueweb-"+k+"K/queries.txt";
		String compareToFile = "src/es/uam/eps/bmi/clueweb-"+k+"K/relevance.txt";
        	//Calculamos resultados
        	ArrayList<ArrayList<String>> resultados = _build_results(outputFile_build, LucIdx, queryFile, max);
		
        	//evaluamos los resultados
        	_evaluate_results(outputFile_evaluate, resultados, max, compareToFile);
	}
	}
    }

 }

