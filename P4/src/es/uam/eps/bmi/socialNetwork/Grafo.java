package es.uam.eps.bmi.socialNetwork;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Grafo {

    private String nombre;
    private UndirectedGraph<String, String> g;
    private HashMap<String, Double> pageRankNodo;
    private HashMap<String, Double> clusteringCoefficientsNodo;
    private HashMap<String, Double> arraigoArco;
    private int numPuentesLocales;
    private double clusteringCoefficientGrafo;
    private double asortatividadGrafo;
    private HashMap<Integer, Integer> distribucionGrado;
    private HashMap<String, Double> betweenessCentralityNodo;
    private double mediaGrado;
    private double mediaGradoVecinos;
    private boolean paradojaAmistad;

    public Grafo(String nombre, UndirectedGraph<String, String> g) {
        this.nombre = nombre;
        this.g = g;
        this.pageRankNodo = new HashMap<String, Double>();
        this.clusteringCoefficientsNodo = new HashMap<String, Double>();
        this.arraigoArco = new HashMap<String, Double>();
        this.numPuentesLocales = 0;
        this.clusteringCoefficientGrafo = 0;
        this.asortatividadGrafo = 0;
        this.distribucionGrado = new HashMap<Integer, Integer>();
        this.betweenessCentralityNodo =  new HashMap<String, Double>();
        this.paradojaAmistad = false;
        this.mediaGrado = 0.0;
        this.mediaGradoVecinos = 0.0;
    }

    public void calcularMetricas() {
        System.out.println("\nCalcular métricas del grafo " + this.nombre + "...");
        this.calcularClusteringCoefficientsNodo();
        this.calcularPageRankNodo();
        this.calcularArraigoArcoYPuentesLocales();
        this.calcularClusteringCoefficientGrafo();
        this.calcularAsortativityGrafo();
        this.calcularDistribucionGradoGrafo();
        this.calcularBetweenessNodo();
        this.calcularMediasGradoVecinos();
        this.calcularParadojaAmistad();
    }

    private void calcularPageRankNodo()  {
        PageRank<String, String> pr = new PageRank<String, String>(this.g, 0.15);
        pr.initialize();
        pr.setMaxIterations(200);
        pr.evaluate();
        for (String e : this.g.getVertices()) {
            this.pageRankNodo.put(this.nombre + " " + e, pr.getVertexScore(e));
        }
        System.out.println("PageRank de los nodos del grafo " + this.nombre + " calculado");
    }

    private void calcularClusteringCoefficientsNodo() {
        double count = 0;
        double grado = 0;
        double resultado = 0;

        for (String v : this.g.getVertices()) {
            count = 0;
            Collection<String> setVecinos = this.g.getNeighbors(v);
            for (String vecino : this.g.getNeighbors(v)) {
                Collection<String> setVecinosVecino = new ArrayList<String>(this.g.getNeighbors(vecino));
                setVecinosVecino.retainAll(setVecinos);
                count = count + setVecinosVecino.size();
            }

            grado = (double) this.g.degree(v);
            if (grado <= 1) {
                resultado = 0;
            } else {
                resultado = (count / 2) / (grado * (grado - 1) / 2);
            }
            this.clusteringCoefficientsNodo.put(this.nombre + " " + v, resultado);
        }
        System.out.println("Coeficientes de clustering de los nodos del grafo " + this.nombre + " calculado");
    }

    private void calcularArraigoArcoYPuentesLocales() {
        double comun;
        double resultado;

        for (String e : this.g.getEdges()) {
            Pair<String> arco = this.g.getEndpoints(e);
            Collection<String> setVecinos1 = this.g.getNeighbors(arco.getFirst());
            Collection<String> setVecinos2 = new ArrayList<String>(this.g.getNeighbors(arco.getSecond()));
            setVecinos2.retainAll(setVecinos1);
            comun = setVecinos2.size();
            if (comun == 0) {
                this.numPuentesLocales++;
                resultado = 0;
            } else{
                resultado = comun / ((double) (this.g.degree(arco.getFirst()) - 1) + (double) (this.g.degree(arco.getSecond()) - 1) - comun);
            }   
            this.arraigoArco.put(this.nombre + " " + e, resultado);           
        }
        System.out.println("Arraigo (embededness) de los arcos y puentes locales del grafo " + this.nombre + " calculado");
    }

    private void calcularClusteringCoefficientGrafo() {
        double suma = 0;

        for (Double r : this.clusteringCoefficientsNodo.values()) {
            suma = suma + r;
        }
        this.clusteringCoefficientGrafo = suma / this.clusteringCoefficientsNodo.values().size();

        System.out.println("Coeficiente de clustering global del grafo " + this.nombre + " calculado");
    }

    private void calcularAsortativityGrafo() {
        double suma = 0.0;
        double total;
        double aux = 0.0;
        Integer gv1, gv2;

        for (String s : this.g.getEdges()) {
            Pair<String> arco = this.g.getEndpoints(s);
            gv1 = this.g.degree(arco.getFirst());
            gv2 = this.g.degree(arco.getSecond());
            suma = suma + gv1 * gv2;
        }
        total = 4 * this.g.getEdgeCount() * suma;

        for (String e : this.g.getVertices()) {
            gv1 = this.g.degree(e);
            aux = aux + gv1 * gv1;
        }
        aux = aux * aux;

        suma = 0.0;
        for (String e : this.g.getVertices()) {
            gv1 = this.g.degree(e);
            suma = suma + gv1 * gv1 * gv1;
        }
        suma = 2 * this.g.getEdgeCount() * suma;
        total = (total - aux) / (suma - aux);

        this.asortatividadGrafo = total;
        System.out.println("Asortatividad del grafo " + this.getNombre() + " calculada");
    }

    private void calcularDistribucionGradoGrafo() {
        Integer nv;
        Integer gv;

        for (String e : this.g.getVertices()) {
            gv = this.g.degree(e);
            if (this.distribucionGrado.containsKey(gv)) {
                nv = this.distribucionGrado.get(gv);
                this.distribucionGrado.replace(gv, nv + 1);
            } else {
                this.distribucionGrado.put(gv, 1);
            }
        }
        
        System.out.println("Distribución del grado del grafo " + this.getNombre() + " calculada");
    }

    private void calcularBetweenessNodo() {

        BetweennessCentrality bc = new BetweennessCentrality(this.g, true, false);
        bc.setRemoveRankScoresOnFinalize(false);
        bc.evaluate();
        
        for (String v : this.g.getVertices()) {
            this.betweenessCentralityNodo.put(this.nombre+ " " + v, bc.getVertexRankScore(v));
        }
        System.out.println("Betweenness del grafo " + this.nombre + " calculado");
    }
    
    private void calcularMediasGradoVecinos(){
        double numVecinos = 0.0;
        double numVecinosVecino, mediaVecinosVecino;
        double numMediaVecinosVecinos = 0.0;
        
        for(String v: this.g.getVertices()){
            numVecinos = numVecinos + this.g.degree(v);
            numVecinosVecino = 0.0;
            for(String vv: this.g.getNeighbors(v)){
                numVecinosVecino = numVecinosVecino + this.g.degree(vv);
            }
            if(this.g.degree(v)==0){
                mediaVecinosVecino = 0.0;
            } else{
                mediaVecinosVecino = numVecinosVecino/this.g.degree(v);
            }
            numMediaVecinosVecinos = numMediaVecinosVecinos + mediaVecinosVecino;
        }
        
        this.mediaGrado = numVecinos/this.g.getVertexCount();
        this.mediaGradoVecinos = numMediaVecinosVecinos/this.g.getVertexCount();
        
        System.out.println("Medias de grados de vecinos del grafo " + this.nombre + " calculadas");        
    }
    
    public void calcularParadojaAmistad(){
        if(this.mediaGrado <= this.mediaGradoVecinos){
            this.paradojaAmistad = true;
            System.out.println("Sí se cumple la paradoja de la amistad: "+this.mediaGrado+" <= "+this.mediaGradoVecinos);
        } else{
            this.paradojaAmistad = false;
            System.out.println("No se cumple la paradoja de la amistad: "+this.mediaGrado+">= "+this.mediaGradoVecinos);
        }
    }


    public void pintarGrafo() {
        System.out.println("\nGrafo " + this.nombre + ":\n" + this.g.toString());
        System.out.println("PageRank de los nodos: " + this.pageRankNodo.toString());
        System.out.println("Coeficientes de clustering de los nodos: " + this.clusteringCoefficientsNodo.toString());
        System.out.println("Arraigo (embededness) de los arcos: " + this.arraigoArco.toString());
        System.out.println("Número de puentes locales: " + this.numPuentesLocales);
        System.out.println("Coeficiente de clustering global: " + this.clusteringCoefficientGrafo);
        System.out.println("Asortatividad del grafo: " + this.asortatividadGrafo);
        System.out.println("Distribción del grado del grafo: " + this.distribucionGrado.toString());
        System.out.println("Betweeness de los nodos: "+this.betweenessCentralityNodo.toString());
        if (this.paradojaAmistad){
            System.out.println("Sí se cumple la paradoja de la amistad: "+this.mediaGrado+" <= "+this.mediaGradoVecinos);
        } else{
            System.out.println("No se cumple la paradoja de la amistad: "+this.mediaGrado+">= "+this.mediaGradoVecinos);
        }
        
    }
    
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UndirectedGraph<String, String> getG() {
        return g;
    }

    public void setG(UndirectedGraph<String, String> g) {
        this.g = g;
    }

    public HashMap<String, Double> getPageRankNodo() {
        return pageRankNodo;
    }

    public void setPageRankNodo(HashMap<String, Double> pageRankNodo) {
        this.pageRankNodo = pageRankNodo;
    }

    public HashMap<String, Double> getClusteringCoefficientsNodo() {
        return clusteringCoefficientsNodo;
    }

    public void setClusteringCoefficientsNodo(HashMap<String, Double> clusteringCoefficientsNodo) {
        this.clusteringCoefficientsNodo = clusteringCoefficientsNodo;
    }

    public HashMap<String, Double> getArraigoArco() {
        return arraigoArco;
    }

    public void setArraigoArco(HashMap<String, Double> arraigoArco) {
        this.arraigoArco = arraigoArco;
    }

    public int getNumPuentesLocales() {
        return numPuentesLocales;
    }

    public void setNumPuentesLocales(int numPuentesLocales) {
        this.numPuentesLocales = numPuentesLocales;
    }

    public double getClusteringCoefficientGrafo() {
        return clusteringCoefficientGrafo;
    }

    public void setClusteringCoefficientGrafo(double clusteringCoefficientGrafo) {
        this.clusteringCoefficientGrafo = clusteringCoefficientGrafo;
    }

    public double getAsortatividadGrafo() {
        return asortatividadGrafo;
    }

    public void setAsortatividadGrafo(double asortatividadGrafo) {
        this.asortatividadGrafo = asortatividadGrafo;
    }

    public HashMap<Integer, Integer> getDistribucionGrado() {
        return distribucionGrado;
    }

    public void setDistribucionGrado(HashMap<Integer, Integer> distribuciónGrado) {
        this.distribucionGrado = distribuciónGrado;
    }

    public HashMap<String, Double> getBetweenessCentralityNodo() {
        return betweenessCentralityNodo;
    }

    public void setBetweenessCentralityNodo(HashMap<String, Double> betweenessCentralityNodo) {
        this.betweenessCentralityNodo = betweenessCentralityNodo;
    }

    public double getMediaGrado() {
        return mediaGrado;
    }

    public void setMediaGrado(double mediaGrado) {
        this.mediaGrado = mediaGrado;
    }

    public double getMediaGradoVecinos() {
        return mediaGradoVecinos;
    }

    public void setMediaGradoVecinos(double mediaGradoVecinos) {
        this.mediaGradoVecinos = mediaGradoVecinos;
    }

    public boolean isParadojaAmistad() {
        return paradojaAmistad;
    }

    public void setParadojaAmistad(boolean paradojaAmistad) {
        this.paradojaAmistad = paradojaAmistad;
    }

    
}
