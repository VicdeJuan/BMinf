/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.QueryParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dani
 */
public class LiteralMatchingSearcher implements Searcher {

    BasicReader indice;
    private String indexdir;
    private final static int TOP = 5;

    public static void main(String[] args) throws IOException {

        String inputCollectionPath = "indice.txt";
        String outputCollectionPath = "querys.txt";

        BasicIndex basicIdx = new BasicIndex();

        TFIDFSearcher tfSearch = new TFIDFSearcher();
        tfSearch.build(basicIdx);
        //ahora leemos de teclado las querys
        System.out.println("Introducir las palabras de la búsqueda:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String query = br.readLine();
        String prueba = "a b";
        List<ScoredTextDocument> search = tfSearch.search(prueba);

        List<ScoredTextDocument> resul = tfSearch.search(query);
        if (resul != null && resul.size() > 0) {
            for (int i = 0; i < TOP; i++) {
                System.out.println(resul.get(i).getDocId());
            }

        } else {
            System.out.println("Consulta vacia");
        }

    }

    @Override
    public void build(Index index) {
        this.indexdir = index.getPath();

        try {
            this.indice = new BasicReader(((BasicIndex) index).getPath());
        } catch (IOException ex) {
            Logger.getLogger(LiteralMatchingSearcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LiteralMatchingSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    //Esta la normal, no literal
    public List<ScoredTextDocument> search(String query) {
        List<ScoredTextDocument> toret = null;
        BinaryHeap heap;
        String[] querys;
        TreeMap<String, Double> doctf_id = new TreeMap();

        // hacer un query parser 
        querys = new QueryParser().parses(query);
        //Voy a ir discriminando los postings que no cumplan el literal
        List<Posting> termsprincipal = indice.getTermPostings(querys[0]);
        for (Posting post : termsprincipal) {
            double tf_idf = tf_idf(querys[0], post.getDocId());
            if (!doctf_id.containsKey(querys[0])) {

                doctf_id.put(post.getDocId(), tf_idf);
            }
            double old = doctf_id.get(querys[0]);
            old += tf_idf;
            doctf_id.put(querys[0], old);
        }

        String[] querysaux = new String[querys.length - 1];
        //quiero quitar el primer termino de la query
        for (int i = 0; i < querys.length; i++) {
            querysaux[i] = querys[i + 1];
        }
        // vamos descartando los Postings que no cumplan la busqueda literal, los buenos se quedan en nuevaprincipal
        for (String qaux : querysaux) {
            List<Posting> termPostings = indice.getTermPostings(qaux);
            List<Posting> nuevaprincipal = null;

            for (Posting postprincipal : termsprincipal) {
                for (Posting acomparar : termPostings) {
                    List<Long> posicionesLiteral = postprincipal.posicionesLiteral(acomparar);
                    if (postprincipal.getDocId().equals(acomparar.getDocId())) {

                        Posting sobrevive = new Posting(postprincipal.getDocId(), posicionesLiteral);
                        nuevaprincipal.add(sobrevive);
                    }
                }
            }
            termsprincipal = nuevaprincipal;
            for (Posting post : termsprincipal) {
                double tf_idf = tf_idf(qaux, post.getDocId());
                if (!doctf_id.containsKey(querys[0])) {

                    doctf_id.put(post.getDocId(), tf_idf);
                }
                double old = doctf_id.get(qaux);
                old += tf_idf;
                doctf_id.put(qaux, old);
            }
        }
        for (String docid : doctf_id.keySet()) {
            double old = doctf_id.get(docid);
            Object get = this.indice.getDiccionario_docId_modulo().get(docid);
            String a = get.toString();
            double modulo = Double.parseDouble(a);
            old = old / modulo;
            //comprobar modulo correcto    
            doctf_id.put(docid, old);
        }
        int k = 0;

        for (int g = 0; k < termsprincipal.size(); g++) {
            String doc = termsprincipal.get(g).getDocId();
            double score = doctf_id.get(doc);
            ScoredTextDocument scoretext = new ScoredTextDocument(doc, score);
            toret.add(scoretext);
        }
        Collections.sort(toret);

        return toret.subList(0, TOP);
    }

    /**
     * Computa el producto de tf*id de un término y un documento dado.
     *
     * @param termino Termino.
     * @param docid Id del documento.
     * @return
     */
    public double tf_idf(String termino, String docid) {

        double freq = 0;
        double ndoc = 0;
        double tf = 0;
        double idf = 0;
        List<Posting> termPostings = indice.getTermPostings(termino);
        for (Posting post : termPostings) {
            // Cada vuelta es en un documento distinto.
            if (post.getDocId().equals(docid)) {

                freq = post.getTermFrequency();

                tf = post.getTermFrequency() == 0 ? 1 : 1 + Math.log(post.getTermFrequency()) / Math.log(2);
            }
            ndoc++;

        }
	// val 2 = tf
        //tf = freq == 0 ? 1 : 1+Math.log(freq)/Math.log(2);
        // val 3 = idf = log(nº doc/nºdocs con ese termino)
        idf = Math.log(indice.getNumDoc() / ndoc) / Math.log(2);

        return tf * idf;
    }

}
