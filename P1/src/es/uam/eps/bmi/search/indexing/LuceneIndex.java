/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;

/**
 *
 * @author dani
 */
public class LuceneIndex implements Index {

    String indexPath;
    private IndexReader reader = null;
    private static long num_id = 0;

    /* 
     dos argumentos de entrada: la ruta de la carpeta que contiene la colección de documentos con
     los que crear el índice, y la ruta de la carpeta en la que almacenar el índice creado.*/
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Error en número de argumentos");
        }
        String inputCollectionPath = "src/es/uam/eps/bmi/clueweb-1K";
        String outputCollectionPath = "outputCollection";
        LuceneIndex LucIdx = new LuceneIndex(inputCollectionPath, outputCollectionPath, new HTMLSimpleParser());

    }

    public LuceneIndex(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        this.indexPath = inputCollectionPath;

        build(inputCollectionPath, outputIndexPath, textParser);
    }

    public LuceneIndex() {

    }

    //  docDir coincide con la ruta de los zips? hay que java.util.zip.ZipInputStream?
    // writer es donde se genera el indice. En el path indicado por dir (indexpath), y con la configuracion
    // indicada por iwc?
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        boolean create = true;

        try {
            System.out.println("Indexing to directory '" + inputCollectionPath + "'...");
            this.indexPath = outputIndexPath;
            Directory dir = FSDirectory.open(new File(this.indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);

            final ZipFile docDir = new ZipFile(inputCollectionPath + "/docs.zip");
            if (docDir.entries() == null) {
                System.out.println("Document directory '" + docDir + "' does not exist or is not readable, please check the path");
                System.exit(1);
            }

            if (create) {
        // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocs(writer, docDir, textParser);
            writer.close();

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }

    }

    @Override
    public void load(String indexPath) {
        this.indexPath = indexPath;
        try {
            reader = IndexReader.open(FSDirectory.open(new File(indexPath)));
        } catch (IOException ex) {
            System.out.println(" Loading error " + ex.getMessage());
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getPath() {
        return this.indexPath;

    }

    @Override
    public List<String> getDocIds() {
        List<String> ids = new ArrayList<>();
        IndexReader _reader = null;
        Document doc = null;
        try {
            _reader = IndexReader.open(FSDirectory.open(new File(this.indexPath)));
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (_reader == null) {
            return null;
        }
        for (int i = 1; i < _reader.maxDoc(); i++) {
            try {
                doc = _reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (doc == null) {
                continue;
            }
            ids.add(doc.getFieldable("id").stringValue());
        }

        return ids;

    }

    // hay que coger todos los ids de los documentos del indice, como? busqueda de ids en 
    //indice o recorriendo linea a linea?
    @Override
    public TextDocument getDocument(String docId) {

        if (reader == null) {
            return null;
        }
        TextDocument textdoc;
        Document doc = null;
        String name;
        String id;

        for (int i = 1; i < reader.maxDoc(); i++) {
            try {
                doc = reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (doc == null) {
                continue;
            }
            //si coinciden los id contruimos el TextDocument
            if (doc.getFieldable("id").stringValue().equals(docId)) {
                name = doc.getFieldable("name").stringValue();
                id = doc.getFieldable("id").stringValue();
                textdoc = new TextDocument(id, name);
                return textdoc;

            }
        }
        return null;

    }

    @Override
    public List<String> getTerms() {
        List<String> terms = new ArrayList<>();
        if (reader == null) {
            return null;
        }
        Document doc = null;
        TermEnum termenum= null;
        try {
            termenum=this.getReader().terms();
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while(termenum.next()){
                Term term=termenum.term();
                if(term.field().equals("contents")){
                    terms.add(term.text());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        
/*
        for (int i = 1; i < reader.maxDoc(); i++) {
            try {
                doc = reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (doc == null) {
                continue;
            }
            List<Fieldable> fields = doc.getFields();
                   
            terms.add(doc.getFieldable("contents").stringValue());
        }
        */

        return terms;
    }

    @Override
    public List<Posting> getTermPostings(String term) {
        /*if (reader == null) {
            return null;
        }
        List<Long> pos = new ArrayList<>();
        List<Posting> posts = new ArrayList<>();
        Document doc;

        for (int i = 1; i < reader.maxDoc(); i++) {
            Posting post;
            try {
                doc = reader.document(i);
                Term ter = new Term(term, term);
                Integer aaux = reader.termPositions(ter).nextPosition();
                Long aux = aaux.longValue();
                pos.add(aux);
                post = new Posting(doc.getFieldable("contents").stringValue(),
                        term, pos);
                posts.add(post);

            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        */List<Posting> posts = new ArrayList<>();
        TermEnum termenum= null;
        try {
            termenum=this.getReader().terms();
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while(termenum.next()){
                Term term=termenum.term();
                if(term.field().equals("contents")){
                    if(term.text().equals(term)){
                    
                    }
                    //terms.add(term.text());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        //sacar docid,term,termpositions
        
            //si es el term
          //  if(){
            List<Long> pos = new ArrayList<>();
           // TermPositions termposition= this.getReader().termPositions(new Term("contents",term));
            
            Posting post= new Posting("docid",term,pos);
            posts.add(post);
            
        
        //Posting(String docId, String term, List<Long> termPositions)
        
        return posts;
    }

    /*Así vemos si se ha hecho el load o no*/
    public IndexReader getReader() {
        return this.reader;
    }

    static void indexDocs(IndexWriter writer, ZipFile file, TextParser textParser)
            throws IOException {
        // do not try to index files that cannot be read
        try {

          // make a new, empty document
            ZipFile zipFile = file;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                Document doc = new Document();
                ZipEntry entry = entries.nextElement();

                Field pathField = new Field("name", entry.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
                pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
                doc.add(pathField);

                NumericField idField = new NumericField("id", Field.Store.YES, true);
                idField.setLongValue(num_id);
                num_id++;
                doc.add(idField);

                NumericField modifiedField = new NumericField("modified");
                modifiedField.setLongValue(entry.getTime());
                doc.add(modifiedField);

                InputStream stream = zipFile.getInputStream(entry);
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                String sCurrentLine;
                String content = "";

                while ((sCurrentLine = br.readLine()) != null) {
                    content = content + sCurrentLine;
                }

                String toad = textParser.parse(content);
                doc.add(new Field("contents", toad, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));

                if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                    // New index, so we just add the document (no old document can be there):
                    System.out.println("adding " + entry.getName());
                    writer.addDocument(doc);
                } else {
            // Existing index (an old copy of this document may have been indexed) so 
                    // we use updateDocument instead to replace the old one matching the exact 
                    // path, if present:
                    System.out.println("updating " + entry.getName());
                    writer.updateDocument(new Term("name", entry.getName()), doc);
                }
            }

        } finally {
            file.close();
        }
    }
}
