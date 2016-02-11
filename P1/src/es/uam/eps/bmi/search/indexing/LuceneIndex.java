/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;




/**
 *
 * @author dani
 */
public class LuceneIndex implements Index{
    String indexPath;
    private IndexReader reader = null;
    private static long num_id=0;
    
/* 
    dos argumentos de entrada: la ruta de la carpeta que contiene la colección de documentos con
los que crear el índice, y la ruta de la carpeta en la que almacenar el índice creado.*/

    /**
     *
     * @param inputCollectionPath
     * @param outputCollectionPath
     */
    
    public void main(String inputCollectionPath,String outputCollectionPath){
        
        build(inputCollectionPath,outputCollectionPath,null);
        
     
    }
    
    //  docDir coincide con la ruta de los zips? hay que java.util.zip.ZipInputStream?
    // writer es donde se genera el indice. En el path indicado por dir (indexpath), y con la configuracion
    // indicada por iwc?
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        boolean create=true;
        
     
        try {
      System.out.println("Indexing to directory '" + inputCollectionPath + "'...");
      this.indexPath=outputIndexPath;
      Directory dir = FSDirectory.open(new File(this.indexPath));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);

      final File docDir = new File(inputCollectionPath);
       if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
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
      indexDocs(writer, docDir);
      writer.close();



    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
    
        
    }

    @Override
    public void load(String indexPath) {
        this.indexPath=indexPath;
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
        List<String> ids =new ArrayList<String>();
        IndexReader reader = null;
        Document doc = null;
        try {
            reader = IndexReader.open(FSDirectory.open(new File(this.indexPath)));
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=1; i<reader.maxDoc();i++){
            try {
                doc= reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
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
        TextDocument textdoc = null;
        Document doc = null;
        String name=null;
        String id=null;
        
        for(int i=1; i<reader.maxDoc();i++){
            try {
                doc= reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            //si coinciden los id contruimos el TextDocument
            if(doc.getFieldable("id").stringValue().equals(docId)){
                name = doc.getFieldable("name").stringValue();
                id = doc.getFieldable("id").stringValue();
                textdoc = new TextDocument(id,name);
                return textdoc;
            
            }
        }
        return null;
    
    
    }

    @Override
    public List<String> getTerms() {
        List<String> terms =new ArrayList<String>();
        if (reader == null) {
            return null;
        }
        Document doc = null;
        
        for(int i=1; i<reader.maxDoc();i++){
            try {
                doc= reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            terms.add(doc.getFieldable("name").stringValue());
        }
      
        return terms;
    }

    @Override
    public List<Posting> getTermPostings(String term) {
        if (reader == null) {
            return null;
        }
        
        List<Posting> posts=new ArrayList<Posting>();
        Document doc = null;
        
        for(int i=1; i<reader.maxDoc();i++){
            Posting post;
            try {
                doc= reader.document(i);
            } catch (IOException ex) {
                Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
           /* 
            post=new Posting(doc.getFieldable("name").stringValue(),
                    term, List<Long> termPositions);
            posts.add(post);
                    );*/
        }
        
        return posts;
    }
    
    
    static void indexDocs(IndexWriter writer, File file)
    throws IOException {
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(writer, new File(file, files[i]));
          }
        }
      } else {

        FileInputStream fis;
        try {
          fis = new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help
          return;
        }

        try {

          // make a new, empty document
          Document doc = new Document();

          // Add the path of the file as a field named "path".  Use a
          // field that is indexed (i.e. searchable), but don't tokenize 
          // the field into separate words and don't index term frequency
          // or positional information:
          //we use name as docId
          Field pathField = new Field("name", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(pathField);

          // Add the last modified date of the file a field named "modified".
          // Use a NumericField that is indexed (i.e. efficiently filterable with
          // NumericRangeFilter).  This indexes to milli-second resolution, which
          // is often too fine.  You could instead create a number based on
          // year/month/day/hour/minutes/seconds, down the resolution you require.
          // For example the long value 2011021714 would mean
          // February 17, 2011, 2-3 PM.
          NumericField idField = new NumericField("id", Field.Store.YES, true);
          num_id++;
          idField.setLongValue(num_id);
          doc.add(idField);
          
          
          
          NumericField modifiedField = new NumericField("modified");
          modifiedField.setLongValue(file.lastModified());
          doc.add(modifiedField);

          // Add the contents of the file to a field named "contents".  Specify a Reader,
          // so that the text of the file is tokenized and indexed, but not stored.
          // Note that FileReader expects the file to be in UTF-8 encoding.
          // If that's not the case searching for special characters will fail.
          doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            writer.addDocument(doc);
          } else {
            // Existing index (an old copy of this document may have been indexed) so 
            // we use updateDocument instead to replace the old one matching the exact 
            // path, if present:
            System.out.println("updating " + file);
            writer.updateDocument(new Term("name", file.getPath()), doc);
          }
          
        } finally {
          fis.close();
        }
      }
    }
  }
}
