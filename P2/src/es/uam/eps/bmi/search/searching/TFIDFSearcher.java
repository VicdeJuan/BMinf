package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.QueryParser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TFIDFSearcher implements Searcher {

    BasicReader indice;
    private String indexdir;
    private final static int TOP = 5;
    private final static int MOSTRAR = 20;

    public static void main(String[] args) throws IOException {

        String outputCollectionPath = "idx.txt";

        BasicIndex basicIdx = new BasicIndex();
        boolean build = false;
        // Asi no hay que ir comentando uno o el otro.
        if (build) {
            basicIdx.build("pruebas/clueweb-1K/docs.zip", outputCollectionPath, new HTMLSimpleParser());
        } else {
            basicIdx.load(outputCollectionPath);
        }

        TFIDFSearcher tfSearch = new TFIDFSearcher();
        tfSearch.build(basicIdx);
        //ahora leemos de teclado las querys
        System.out.println("Introducir las palabras de la búsqueda:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String query = br.readLine();
        String[] querys = new QueryParser().parses(query);
        long len = 0;
        byte[] buffer = new byte[2048];
        //String prueba="a b";
        //List<ScoredTextDocument> search = tfSearch.search(query);
        InputStream theFile;
        List<ScoredTextDocument> resul = tfSearch.search(query);
        if (resul != null && resul.size() > 0) {
            int topp = TOP;
            if (resul.size() < TOP) {
                topp = resul.size();
            }
            for (int i = 0; i < topp; i++) {
                String docidd = resul.get(i).getDocId();
                ModuloNombre doc = tfSearch.indice.getDiccionarioDocs_NM().get(docidd);
                String docname = doc.getNombre();
                System.out.println(docname);

                theFile = new FileInputStream("pruebas/clueweb-1K/docs.zip");



                ZipInputStream stream = new ZipInputStream(theFile);
                ZipEntry entry;

                while ((entry = stream.getNextEntry()) != null) {
                    //Cogemos siguiente documento del zip
                    
                    if (entry.getName().equals(docname)) {
                        String value, texto = "";
                        while ((len = stream.read(buffer)) > 0) {
                            value = new String(buffer, 0, (int) len, "UTF-8");
                            texto = texto.concat(value);
                        }

                        String[] split = texto.split(" ");
                        for (int k = 0; k < split.length; k++) {
                            for (String q : querys) {
                                if (split[k].contains(q)) {
                                    for (int j = 0; j < MOSTRAR; j++) {
					    if (split.length >= k+j)
	                                        System.out.print(split[k + j]);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } else {
            System.out.println("Consulta vacia");
        }

    }

    @Override
    public void build(Index index) {
        this.indexdir = index.getPath();
        BasicIndex aux = ((BasicIndex) index);

        try {
            aux.loadReader();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TFIDFSearcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(TFIDFSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.indice = aux.getReader();

    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        List<ScoredTextDocument> toret;
        toret = new ArrayList<>();
        BinaryHeap heap;
        String[] querys;
        TreeMap<String, Double> doctf_id = new TreeMap();

        // hacer un query parser 
        querys = new QueryParser().parses(query);
        //cada qaux es un termino
        for (String qaux : querys) {
            List<Posting> termPostings = indice.getTermPostings(qaux);
            for (Posting post : termPostings) {
                double tf_idf = tf_idf(qaux, post.getDocId());
                if (!doctf_id.containsKey(post.getDocId())) {
                    doctf_id.put(post.getDocId(), tf_idf);
                } else {
                    double old = doctf_id.get(post.getDocId());
                    old += tf_idf;
                    doctf_id.put(post.getDocId(), old);
                }

            }
        }

        for (String docid : doctf_id.keySet()) {
            double old = doctf_id.get(docid);
            ModuloNombre get = this.indice.getDiccionarioDocs_NM().get(docid);
            double modulo = get.getModulo();

            old = old / modulo;
            //Falta comprobar el modulo   

            doctf_id.put(docid, old);
        }
        int k = 1;

        for (String doc : doctf_id.keySet()) {
            if (k <= TOP) {
                ScoredTextDocument aux = new ScoredTextDocument(doc, doctf_id.get(doc));
                toret.add(aux);
                k++;
            } else {
                break;
            }
        }

        return toret;
    }

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
