/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.socialNetwork;

import static cern.clhep.Units.g;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.algorithms.metrics.Metrics;
import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.InstantiateFactory;

/**
 *
 * @author parra
 */
public class AnalisisGrafos {

    final static String RESULTS_PATH = "resultados/";
    final static String RESULTS_PATH_PRGRADO = "resultados/prgrado/";
    final static String RESULTS_PATH_GRADOS = "resultados/grados/";
    final static String RESULTS_PATH_BETWEENESSGRADO = "resultados/betweenessgrado/";
    final static String FILE_ARRAIGO_EMBEDEDNESS = "bmi1415_p4_12_edgeEmbededness.txt";
    final static String FILE_CLUSTERING_NODO = "bmi1415_p4_12_nodeClustering.txt";
    final static String FILE_PAGE_RANK_NODO = "bmi1415_p4_12_nodePageRank.txt";
    final static String FILE_CLUSTERING_GRAPH = "bmi1415_p4_12_graphClustering.txt";
    final static String FILE_ASORTATIVITY_GRAPH = "bmi1415_p4_12_graphAssortativity.txt";
    final static String FILE_PATH = "data/grafos/";
    //final static ArrayList<String> FILES = new ArrayList<String>(
    //        Arrays.asList("BarabasiAlbertGenerator", "small2"));
    final static ArrayList<String> FILES = new ArrayList<String>(
            Arrays.asList("small1", "small2", "twitter", "fb"));
    final static ArrayList<String> RANDOM_GRAPHS = new ArrayList<String>(
            Arrays.asList("erdos", "barabasi"));

    private ArrayList<Grafo> lista_grafos;

    public AnalisisGrafos() {

    }

    public static void main(String[] argv) throws IOException {

        AnalisisGrafos ag = new AnalisisGrafos();

        //Crear grafos:
        System.out.println("Crear grafos...\n");
        ag.crearGrafos();

        //Calcular métricas
        System.out.println("\n\nCalcular métricas...");
        for (Grafo g : ag.getLista_grafos()) {
            g.calcularMetricas();
        }

        //Pintar grafos
        System.out.println("\n\nPintar grafos...");
        for (Grafo g : ag.getLista_grafos()) {
            g.pintarGrafo();
        }

        //Estadísticas de los grafos:
        ag.top10();
        ag.informacionGlobal();
        ag.plots();
        //ag.prueba();

    }

    private ArrayList<String> comparador(HashMap<String, Double> map) {
        Comparator<String> comp = new Comparator<String>() {
            public int compare(String s1, String s2) {
                if ((map.get(s1) - map.get(s2)) < 0) {
                    return 1;
                } else if ((map.get(s1) - map.get(s2)) > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        ArrayList<String> top10 = new ArrayList<String>(map.keySet());
        Collections.sort(top10, comp);

        return top10;
    }
    
    private void prueba() throws IOException {
        ArrayList<String> listaOrdenada;
        HashMap<String, Double> aa = new HashMap<String, Double>();

        for (Grafo g : this.lista_grafos) {
            aa.putAll(g.getArraigoArco());
        }
        System.out.println("\nTOP 10 arraigo de arcos:");
        listaOrdenada = this.comparador(aa);
        FileWriter f = new FileWriter(RESULTS_PATH + FILE_ARRAIGO_EMBEDEDNESS);
        BufferedWriter b = new BufferedWriter(f);
        for (int i = 0; i < 10; i++) {
            b.write(listaOrdenada.get(i) + " " + aa.get(listaOrdenada.get(i)) + "\n");
            System.out.println(listaOrdenada.get(i) + " " + aa.get(listaOrdenada.get(i)));
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_ARRAIGO_EMBEDEDNESS);
        b.close();
    }

    private void top10() throws IOException {
        ArrayList<String> listaOrdenada;
        HashMap<String, Double> prn = new HashMap<String, Double>();
        HashMap<String, Double> ccn = new HashMap<String, Double>();
        HashMap<String, Double> aa = new HashMap<String, Double>();

        for (Grafo g : this.lista_grafos) {
            prn.putAll(g.getPageRankNodo());
            ccn.putAll(g.getClusteringCoefficientsNodo());
            aa.putAll(g.getArraigoArco());
        }

        System.out.println("\nTOP 10 pageRank  de nodo:");
        listaOrdenada = this.comparador(prn);
        FileWriter f = new FileWriter(RESULTS_PATH + FILE_PAGE_RANK_NODO);
        BufferedWriter b = new BufferedWriter(f);
        for (int i = 0; i < 10; i++) {
            b.write(listaOrdenada.get(i) + " " + prn.get(listaOrdenada.get(i)) + "\n");
            System.out.println(listaOrdenada.get(i) + " " + prn.get(listaOrdenada.get(i)));
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_PAGE_RANK_NODO);
        b.close();

        System.out.println("\nTOP 10 coeficiente de clustering de nodo:");
        listaOrdenada = this.comparador(ccn);
        f = new FileWriter(RESULTS_PATH + FILE_CLUSTERING_NODO);
        b = new BufferedWriter(f);
        for (int i = 0; i < 10; i++) {
            b.write(listaOrdenada.get(i) + " " + ccn.get(listaOrdenada.get(i)) + "\n");
            System.out.println(listaOrdenada.get(i) + " " + ccn.get(listaOrdenada.get(i)));
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_CLUSTERING_NODO);
        b.close();

        System.out.println("\nTOP 10 arraigo de arcos:");
        listaOrdenada = this.comparador(aa);
        f = new FileWriter(RESULTS_PATH + FILE_ARRAIGO_EMBEDEDNESS);
        b = new BufferedWriter(f);
        for (int i = 0; i < 10; i++) {
            b.write(listaOrdenada.get(i) + " " + aa.get(listaOrdenada.get(i)) + "\n");
            System.out.println(listaOrdenada.get(i) + " " + aa.get(listaOrdenada.get(i)));
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_ARRAIGO_EMBEDEDNESS);
        b.close();
    }

    private void informacionGlobal() throws IOException {

        System.out.println("\nCoeficiente de clustering global de los grafos: ");
        FileWriter f = new FileWriter(RESULTS_PATH + FILE_CLUSTERING_GRAPH);
        BufferedWriter b = new BufferedWriter(f);
        for (Grafo g : this.lista_grafos) {
            b.write(g.getNombre() + " " + g.getClusteringCoefficientGrafo() + "\n");
            System.out.println(g.getNombre() + " " + g.getClusteringCoefficientGrafo());
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_CLUSTERING_GRAPH);
        b.close();

        System.out.println("\nCoeficiente de asortatividad de los grafos: ");
        f = new FileWriter(RESULTS_PATH + FILE_ASORTATIVITY_GRAPH);
        b = new BufferedWriter(f);
        for (Grafo g : this.lista_grafos) {
            b.write(g.getNombre() + " " + g.getAsortatividadGrafo() + "\n");
            System.out.println(g.getNombre() + " " + g.getAsortatividadGrafo());
        }
        System.out.println("Información volcada en fichero " + RESULTS_PATH + FILE_ASORTATIVITY_GRAPH);
        b.close();

        System.out.println("\nNúmero de puentes locales: ");
        for (Grafo g : this.lista_grafos) {
            System.out.println(g.getNombre() + " " + g.getNumPuentesLocales());
        }

        System.out.println("\nParadoja de la amistad: ");
        for (Grafo g : this.lista_grafos) {
            if (g.isParadojaAmistad()) {
                System.out.println(g.getNombre()+ ": Sí se cumple la paradoja de la amistad: " + g.getMediaGrado() + " <= " + g.getMediaGradoVecinos());
            } else {
                System.out.println(g.getNombre()+ ": No se cumple la paradoja de la amistad: " + g.getMediaGrado() + ">= " + g.getMediaGradoVecinos());
            }
        }
    }
    
    private void plots() throws IOException {
        
        for (Grafo g : this.lista_grafos) {
            FileWriter f = new FileWriter(RESULTS_PATH_GRADOS + g.getNombre() + "_grado.txt");
            BufferedWriter b = new BufferedWriter(f);
            for (Integer grado : g.getDistribucionGrado().keySet()) {
                b.write(grado + "\t" + g.getDistribucionGrado().get(grado)+"\n");
            }
            b.close();
        }
        System.out.println("\nVolcados datos para generar gráficas de distribución de grado en el directorio "+RESULTS_PATH_GRADOS);
                
        for (Grafo g : this.lista_grafos) {
            FileWriter f = new FileWriter(RESULTS_PATH_PRGRADO + g.getNombre() + "_grado-pr.txt");
            BufferedWriter b = new BufferedWriter(f);
            for (String v : g.getG().getVertices()) {
                b.write(g.getG().degree(v)+"\t"+ g.getPageRankNodo().get(g.getNombre()+" "+v) + "\n");
            }
            b.close();
        }
        System.out.println("\nVolcados datos para generar gráficas de grado - pagerank "+RESULTS_PATH_PRGRADO);
                
        for (Grafo g : this.lista_grafos) {
            FileWriter f = new FileWriter(RESULTS_PATH_BETWEENESSGRADO + g.getNombre() + "_grado-betweeness.txt");
            BufferedWriter b = new BufferedWriter(f);
            for (String v : g.getG().getVertices()) {
                b.write(g.getG().degree(v)+"\t"+g.getBetweenessCentralityNodo().get(g.getNombre()+" "+v) + "\n");
            }
            b.close();
        }
        System.out.println("\nVolcados datos para generar gráficas de grado - betweeness "+RESULTS_PATH_BETWEENESSGRADO);
    }

    private void crearGrafos() throws FileNotFoundException, IOException {
        String cadena;
        int i = 0;
        String[] vertices = new String[2];
        String v1, v2;
        ArrayList<Grafo> lista_grafos = new ArrayList<Grafo>();

        for (String archivo : FILES) {
            UndirectedGraph<String, String> g = new UndirectedSparseGraph<String, String>();

            FileReader f = new FileReader(FILE_PATH + archivo + ".csv");
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
                vertices = cadena.split(",");
                v1 = vertices[0];
                v2 = vertices[1];

                if (!g.containsVertex(v1)) {
                    g.addVertex(v1);
                }
                if (!g.containsVertex(v2)) {
                    g.addVertex(v2);
                }
                g.addEdge("e_" + i + "-" + v1 + "-" + v2, v1, v2);
                i++;
            }
            b.close();
            lista_grafos.add(new Grafo(archivo, g));
            System.out.println("Creado grafo: " + archivo);
        }

        //Creamos el grafo ErdosRenyi
        Factory<UndirectedGraph<String, String>> graphFactory = UndirectedSparseGraph.getFactory();
        //Factory<String> vertexFactory = InstantiateFactory.getInstance(String.class, typesStr, strs);
        Factory<String> vertexFactory
                = new Factory<String>() {
                    int count;

                    public String create() {
                        count++;
                        return "v" + count;
                    }
                };
        //Factory<String> edgeFactory = InstantiateFactory.getInstance(String.class, typesStr2, strs2);
        Factory<String> edgeFactory
                = new Factory<String>() {
                    int count;

                    public String create() {
                        count++;
                        return "e" + count;
                    }
                };

        ErdosRenyiGenerator<String, String> erg = new ErdosRenyiGenerator<String, String>(graphFactory, vertexFactory,
                edgeFactory, 2000, 0.25);
        Graph g = erg.create();
        UndirectedGraph gu = DirectionTransformer.toUndirected(g, graphFactory, edgeFactory, false);
        System.out.println("Creado grafo: " + RANDOM_GRAPHS.get(0));
        lista_grafos.add(new Grafo(RANDOM_GRAPHS.get(0), gu));

        //Creamos el grafo barabasiAlbertGenerator
        Factory<Graph<String, String>> graphFactory2 = SparseGraph.getFactory();

        //Factory<String> vertexFactory = InstantiateFactory.getInstance(String.class, typesStr, strs);
        Factory<String> vertexFactory2
                = new Factory<String>() {
                    int count;

                    public String create() {
                        count++;
                        return "v" + count;
                    }
                };

        //Factory<String> edgeFactory = InstantiateFactory.getInstance(String.class, typesStr2, strs2);
        Factory<String> edgeFactory2
                = new Factory<String>() {
                    int count;

                    public String create() {
                        count++;
                        return "e" + count;
                    }
                };

        HashSet<String> seedVertices = new HashSet();
        for (i = 0; i < 10; i++) {
            seedVertices.add("va" + i);
        }

        BarabasiAlbertGenerator<String, String> bag = new BarabasiAlbertGenerator<String, String>(graphFactory2, vertexFactory2,
                edgeFactory2, 2000, 50, seedVertices);
        bag.evolveGraph(100);
        Graph g2 = bag.create();
        UndirectedGraph gu2 = DirectionTransformer.toUndirected(g2, graphFactory, edgeFactory, false);
        System.out.println("Creado grafo: " + RANDOM_GRAPHS.get(1));
        lista_grafos.add(new Grafo(RANDOM_GRAPHS.get(1), gu2));

        this.lista_grafos = lista_grafos;
    }

    public ArrayList<Grafo> getLista_grafos() {
        return lista_grafos;
    }

    public void setLista_grafos(ArrayList<Grafo> lista_grafos) {
        this.lista_grafos = lista_grafos;
    }

}