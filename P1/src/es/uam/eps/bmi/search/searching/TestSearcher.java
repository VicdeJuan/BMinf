/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dani
 */
public class TestSearcher {

    public static void main(String[] args) throws IOException {
        /*if (args.length != 1) {
         System.out.println("Error index_path\n");
         return;
         }*/

        String indexPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(indexPath);
        String k = "10K";
        int max = 5;
        FileWriter fichero = new FileWriter("src/es/uam/eps/bmi/Querys"+k+".txt");
	String queryFile = "src/es/uam/eps/bmi/clueweb-"+k+"/queries.txt";
        FileReader f = new FileReader("src/es/uam/eps/bmi/clueweb-"+k+"/relevance.txt");
        BufferedReader b = new BufferedReader(f);
        FileWriter out = new FileWriter("src/es/uam/eps/bmi/Comparacion"+max+"-"+k+".txt");
        
        final PrintWriter pw = new PrintWriter(fichero);

        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            
            
	    int indice=1;
	    
            pw.println(" querys Top " + max);
            
            ArrayList<ArrayList<String>> resultados = new ArrayList<>();
            for (String query : Files.readAllLines(Paths.get(queryFile))) {
                List<ScoredTextDocument> resul = lucSearch.search(query.substring(query.indexOf(":")+1));
                pw.println(query + " -- " + (indice++) + ":");

                if (resul != null && resul.size() > 0) {
                    ArrayList<String> aux = new ArrayList<>();
                    String toadd;
                    for (int i = 0; i < max; i++) {
                        toadd = resul.get(i).getDocId().substring(0,resul.get(i).getDocId().lastIndexOf("."));
                        aux.add(toadd);
                        pw.write(toadd);
                    }
                    resultados.add(aux);
                } else {
                    ArrayList<String> aux = new ArrayList<>();
                    aux.add("Query Vacia");
                    resultados.add(aux);
                    pw.write("Query vacia");
                }
            }

            fichero.close();
        
        
            final PrintWriter pwout = new PrintWriter(out);

            Integer[] encontrados = new Integer[resultados.size()];
            for (int x = 0; x < encontrados.length; x++) {
                encontrados[x] = 0;
            }
            int j = 0;
            String cadena;
            while ((cadena = b.readLine()) != null) {

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
            out.close();
            fichero.close();
        }
    }

 }

