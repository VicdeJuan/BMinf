/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.LuceneIndex;
import es.uam.eps.bmi.search.parsing.HtmlParser;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author dani
 */
public class LuceneSearcher implements Searcher {

    private String indexdir;
    private IndexSearcher indexSearcher;
    private final int TOP5 = 5;
    private final int TOP10 = 10;

    /**
     * recibe como argumento de entrada la ruta de la carpeta que contenga un
     * índice Lucene, y que de forma iterativa pida al usuario consultas a
     * ejecutar por el buscador sobre el índice y muestre por pantalla los top 5
     * documentos devueltos por el buscador para cada consulta
     *
     * @param inputCollectionPath
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Error index_path\n");
            return;
        }

        String indexPath = args[0];
        String outputCollectionPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex();

        LucIdx.load(indexPath);
        if (LucIdx.getReader() != null) {
            LuceneSearcher lucSearch = new LuceneSearcher();
            lucSearch.build(LucIdx);
            //ahora leemos de teclado las querys
            System.out.println("Introducir las palabras de la búsqueda:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String query = br.readLine();

            List<ScoredTextDocument> resul = lucSearch.search(query);
            if (resul != null) {
                for (int i = 0; i < resul.size(); i++) {
                    ScoredTextDocument hit = resul.get(i);
                    TextDocument dochit = LucIdx.getDocument(hit.getDocId());
                    if (dochit != null) {
                        System.out.println(dochit.getName());
                    }
                }

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
        List<ScoredTextDocument> scored = new ArrayList<ScoredTextDocument>();
        Directory dir = null;

        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

        Query q = null;
        try {
            q = new QueryParser(Version.LUCENE_35, "title", analyzer).parse(query);
        } catch (ParseException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LuceneSearcher");
            return null;
        };
        TopDocs top = null;
        try {
            //Finds the top n hits for query.
            top = this.indexSearcher.search(q, null, TOP10);
        } catch (IOException ex) {
            Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LuceneSearcher");
            return null;
        }
        for (int j = 0; j < 1000; j++) {
            Document aux = null;
            try {
                //aux=reader.document(top.scoreDocs[j].doc);
                aux = this.indexSearcher.doc(top.scoreDocs[j].doc);
            } catch (IOException ex) {
                Logger.getLogger(LuceneSearcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            String docPath = aux.getFieldable("name").stringValue();
            ScoredTextDocument textdoc = new ScoredTextDocument(docPath, top.scoreDocs[j].score);
            scored.add(textdoc);
        }
        Collections.sort(scored);

        return scored;
    }

}
