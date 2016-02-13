/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

/**
 *
 * @author dani
 */
public class LuceneSearcher implements Searcher {

    private String indexdir;
    private IndexSearcher indexSearcher;
    private static final int TOP5 = 5;
    private static final int TOP10 = 10;

    /**
     * Recibe como argumento de entrada la ruta de la carpeta que contenga un
     * índice Lucene.
     * De forma iterativa pide al usuario consultas a
     * ejecutar por el buscador sobre el índice y muestre por pantalla el top 5
     * de documentos devueltos por el buscador para cada consulta.
     *
	 * @param args 
	 * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        String indexPath = args.length > 0 ? args[1] : "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(indexPath);

        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            //ahora leemos de teclado las querys
            System.out.println("Introducir las palabras de la búsqueda:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String query = br.readLine();

            List<ScoredTextDocument> resul = lucSearch.search(query);
            if (resul != null && resul.size() >0) {
		    for (int i = 0; i<TOP5; i++)
			    System.out.println(resul.get(i).getDocId());

            } else {
                System.out.println("Consulta vacia");
            }

        }

    }

    @Override
    public void build(Index index) {
        this.indexdir = index.getPath();
        LuceneIndex luceneIndex = (LuceneIndex) index;
        this.indexSearcher = new IndexSearcher(luceneIndex.getReader());

    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        List<ScoredTextDocument> scored = new ArrayList<>();
        Directory dir = null;

        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

        Query q;
        try {
            q = new QueryParser(Version.LUCENE_35, Utils.STR_CONTENT, analyzer).parse(query);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LuceneSearcher");
            return null;
        }
        TopDocs top;
        try {
            //Finds the top n hits for query.
            top = this.indexSearcher.search(q, null, this.indexSearcher.maxDoc());
        } catch (IOException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LuceneSearcher");
            return null;
        }
	
	for(ScoreDoc d : top.scoreDocs){
            Document aux = null;
            try {
                //aux=reader.document(top.scoreDocs[j].doc);
                aux = this.indexSearcher.doc(d.doc);
            } catch (IOException ex) {
                Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            }
	    if (aux == null) continue;
            String docPath = aux.getFieldable(Utils.DOC_STR_NAME).stringValue();
            ScoredTextDocument textdoc = new ScoredTextDocument(docPath, d.score);
            scored.add(textdoc);
        }
        Collections.sort(scored);

        return scored;
    }

}
