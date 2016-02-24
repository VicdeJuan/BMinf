package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.TextParser;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class LuceneIndex implements Index {

	String indexPath;
	private static long num_id = 0;


	/*    Método main: recibe la ruta de la carpeta que contiene la colección 
	 *  de documentos con los que crear el índice, y la ruta de la carpeta 
	 *  en la que almacenar el índice creado.
	 *    Por defecto, utilizar HtmlParser
	 * @param args Argumentos a recibir.
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Error en número de argumentos\nUso: java LuceneIndex inputCollectionPath outputCollectionPath");
		}
		String inputCollectionPath = args[1];
		String outputCollectionPath = args[2];
	}

	/**
	 * Constructor que crea y carga un indice a partir de un indice en
	 * disco.
	 *
	 * @param indexPath Path donde se encuentra el indice en disco.
	 */
	public LuceneIndex(String indexPath) {
		load(indexPath);
	}

	public LuceneIndex(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
		this.indexPath = inputCollectionPath;

		build(inputCollectionPath, outputIndexPath, textParser);
	}

	/**
	 * Construye un indice utilizando los argumentos dados.
	 *
	 * @param inputCollectionPath
	 * @param outputIndexPath
	 * @param textParser
	 */
	@Override
	public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {

		try {
			System.out.println("Indexing to directory '" + inputCollectionPath + "'...");
			this.indexPath = outputIndexPath;
			Directory dir = FSDirectory.open(new File(this.indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);

			final ZipFile docDir = new ZipFile(inputCollectionPath + "/docs.zip");
			if (docDir.entries() == null) {
				System.out.println("Document directory '" + docDir + "' does not exist or is not readable, please check the path");
				System.exit(1);
			}

			iwc.setOpenMode(OpenMode.CREATE);

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir, textParser);
			writer.close();

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
				+ "\n with message: " + e.getMessage());
			System.exit(1);
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
			ids.add(doc.getFieldable(Utils.STR_ID).stringValue());
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
			if (doc.getFieldable(Utils.STR_ID).stringValue().equals(docId)) {
				name = doc.getFieldable(Utils.STR_NAME).stringValue();
				id = doc.getFieldable(Utils.STR_ID).stringValue();
				textdoc = new TextDocument(id, name);
				return textdoc;

			}
		}
		return null;

	}

	@Override
	public List<String> getTerms() {
		List<String> terms = new ArrayList<>();
		// Eliminamos duplicados utilizando un set.
		TreeSet<String> _terms = new TreeSet<>();
		for (int doc = 0; doc < this.reader.maxDoc(); doc++) {
			try {
				TermFreqVector vector = this.reader.getTermFreqVector(doc, "contents");
				_terms.addAll(Arrays.asList(vector.getTerms()));
			} catch (IOException ex) {
				Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		terms.addAll(_terms);
		return terms;

	}

	@Override
	public List<Posting> getTermPostings(String term) {
		List<Posting> posts = new ArrayList<>();
		TermEnum termenum = null;
		try {
			termenum = this.getReader().terms();
			TermPositions termposition = this.getReader().termPositions(new Term("contents", term));
			while (termposition.next()) {
				List<Long> pos = new ArrayList<>();
				for (int j = 0; j < termposition.freq(); ++j) {
					pos.add((long) termposition.nextPosition());
				}
				posts.add(new Posting(reader.document(termposition.doc()).getFieldable(Utils.STR_ID).stringValue(), term, pos));

			}
		} catch (IOException ex) {
			Logger.getLogger(LuceneIndex.class.getName()).log(Level.SEVERE, null, ex);
		}

		return posts;
	}

	/**
	 *
	 * @return El reader del indice. Null si no se ha cargado el indice.
	 */
	public IndexReader getReader() {
		return this.reader;
	}

	/**
	 * Indexa los documentos en el indice.
	 *
	 * @param writer IndexWriter para poder escribir en el indice.
	 * @param file Archivo Zip con los documentos (sin subcarpetas).
	 * @param textParser EL parser para tratar el contenido de los
	 * documentos.
	 * @throws IOException
	 */
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

				Field pathField = new Field(Utils.STR_NAME, entry.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
				pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
				doc.add(pathField);

				NumericField idField = new NumericField(Utils.STR_ID, Field.Store.YES, true);
				idField.setLongValue(num_id);
				num_id++;
				doc.add(idField);

				NumericField modifiedField = new NumericField(Utils.STR_MODIFIED);
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
				doc.add(new Field(Utils.STR_CONTENT, toad, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));

				writer.addDocument(doc);
			}

		} finally {
			file.close();
		}
	}

	/**
	 * 
	 * @return El número de documentos del índice.
	 */
	public int getNumDoc() {
		return this.reader.maxDoc();
	}
}
