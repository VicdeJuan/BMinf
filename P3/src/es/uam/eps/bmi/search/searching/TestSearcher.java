package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSearcher {

    public static void _evaluate_results(ArrayList<ArrayList<String>> resultados, int max, String compareToFile) throws IOException {
        BufferedReader expected_file = new BufferedReader(new FileReader(compareToFile));
        Integer[] encontrados = new Integer[resultados.size() + 1];
        for (int x = 0; x < encontrados.length; x++) {
            encontrados[x] = 0;
        }
        int j = 0;
        String cadena;
        while ((cadena = expected_file.readLine()) != null) {

            String[] split = cadena.split("\t");
            List<String> keys = new ArrayList<>(Arrays.asList(split));
            for (int l = 0; l < Math.min(max, resultados.get(j).size()); l++) {

                //vemos si cada elemento de la query esta en relevance
                for (String key : keys) {
                    if (key.equals(resultados.get(j).get(l))) {
                        encontrados[j]++;
                        break;
                    }
                }

            }
            System.out.println("Query: " + (j + 1) + "\t" + encontrados[j] * 1.0 / max);

            j++;
        }
    }

    /**
     * Construye el fichero oportuno a partir de las busquedas segun los
     * argumentos aportados
     *
     * @param outputFile Fichero en el que almacenar la informacion
     * @param LucIdx Indice de Lucene.
     * @param queryFile Fichero con las consultas.
     * @param max Numero maximo de resultados.
     * @return La lista de los documentos que corresponden a la query.
     * @throws IOException
     */
    private static ArrayList<ArrayList<String>> _build_results(BasicIndex basicIdx, String queryFile, int max) throws IOException {

        ArrayList<ArrayList<String>> resultados = new ArrayList<>();
        if (basicIdx.getReader() != null) {
            // Creamos la clase de búsqueda
            ProximalSearcher ps = new ProximalSearcher();

            //Cargamos el indice en la clase, en una variable BasicReader que permite leer el índice
            ps.build(basicIdx);

            int indice = 1;

            System.out.println(" querys Top " + max);

            for (String query : Files.readAllLines(Paths.get(queryFile))) {
                List<ScoredTextDocument> resul = ps.search(query.substring(query.indexOf(":") + 1));
                System.out.println(query + " -- " + (indice++) + ":");

                if (resul != null && resul.size() > 0) {
                    ArrayList<String> aux = new ArrayList<>();
                    String toadd;
                    for (int i = 0; i < Math.min(max, resul.size()); i++) {
                        System.out.println("Documento: "+ps.getDocName(resul.get(i).getDocId()));
                        toadd = ps.getDocName(resul.get(i).getDocId()).substring(0, ps.getDocName(resul.get(i).getDocId()).lastIndexOf("."));
                        aux.add(toadd);
                        System.out.println(toadd + "\t");
                    }
                    resultados.add(aux);
                } else {
                    ArrayList<String> aux = new ArrayList<>();
                    aux.add("Query Vacia");
                    resultados.add(aux);
                    System.out.println("Query vacia\t");
                }
                System.out.println("\n");
            }

        }
        return resultados;
    }

    /**
     * Metodo main del test.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ArrayList<Integer> maxs = new ArrayList<>();
        maxs.add(5);
        maxs.add(10);

        // Directorio en el que se encuentra el índice en disco
        //String indexDir = "/home/parra/Escritorio/p3Bminf/indices/1K/basic/";
        //String indexDir = "/home/parra/Escritorio/p3Bminf/clueweb-1K-new/";
        String indexDir = "colecciones/clueweb-1K/";

        // Cargamos el índice de disco
        BasicIndex basicIdx = new BasicIndex();
        basicIdx.load(indexDir);

        for (int max : maxs) {
            System.out.println("\t" + max);
            //String queryFile = "/home/parra/Escritorio/p3Bminf/clueweb-1K/queries.txt";
            //String compareToFile = "/home/parra/Escritorio/p3Bminf/clueweb-1K/relevance-1K.txt";
            String queryFile = "colecciones/clueweb-1K/queries.txt";
            String compareToFile = "colecciones/clueweb-1K/relevance-1K.txt";
            //Calculamos resultados
            ArrayList<ArrayList<String>> resultados = _build_results(basicIdx, queryFile, max);

            //evaluamos los resultados
            _evaluate_results(resultados, max, compareToFile);
        }

    }

}
