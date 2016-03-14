package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.QueryParser;
import es.uam.eps.bmi.search.parsing.StemParser;
import es.uam.eps.bmi.search.parsing.StopwordsParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.parsing.XMLReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LiteralMatchingSearcher implements Searcher {

	BasicReader indice;
	private String indexdir;
	private final static int TOP = 5;
	private final static int MOSTRAR = 10;

	public static void main(String[] args) throws IOException {


		XMLReader xmlReader = new XMLReader("index-settings.xml");
		String indexDir = xmlReader.getTextValue(Utils.XMLTAG_INDEXFOLDER);
		String collectionPath = xmlReader.getTextValue(Utils.XMLTAG_COLLECTIONFOLDER);
		String collectionZipFile = collectionPath +  Utils.STR_DEFAULT_ZIPNAME;
		String indexFile = indexDir + "/" + Utils.index_file;

		TextParser parser;
		System.out.println("Selecciona método:\n\t1)Básico.\n\t2)Stopwords.\n\t3)Stemming.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int input = Integer.parseInt(br.readLine());
		switch (input) {
			case 1: //Básico
				parser = new HTMLSimpleParser();
				break;
			case 2: // Stopwords
				parser = new StopwordsParser();
				break;
			case 3: // Stemming
				parser = new StemParser();
				break;
			default:
				parser = new HTMLSimpleParser();
				break;
		}

		BasicIndex basicIdx = new BasicIndex();

		//Si no existe, se crea
		boolean build = !(new File(indexFile).exists());
		if (build) {
			basicIdx.build(collectionPath, indexDir, parser);
		} else {
			basicIdx.load(indexDir);
		}

		LiteralMatchingSearcher LMSearch = new LiteralMatchingSearcher();
		LMSearch.build(basicIdx);

		long len = 0;
		byte[] buffer = new byte[2048];
		InputStream theFile;
		//ahora leemos de teclado las querys

		System.out.println("Introducir las palabras de la búsqueda:");
		br = new BufferedReader(new InputStreamReader(System.in));
		String query = br.readLine();

		query = parser.parse(query);
		String[] querys = new QueryParser().parses(parser.parse(query));
		List<ScoredTextDocument> resul = LMSearch.search(query);

		/*
		 String prueba = "a b";
		 List<ScoredTextDocument> resul = LMSearch.search(prueba);
		 */
		if (resul != null && resul.size() > 0) {
			int topp = TOP;
			if (resul.size() < TOP) {
				topp = resul.size();
			}
			for (int i = 0; i < topp; i++) {
				//System.out.println(resul.get(i).getDocId());
				String docidd = resul.get(i).getDocId();
				ModuloNombre doc = LMSearch.indice.getDiccionarioDocs_NM().get(docidd);
				String docname = doc.getNombre();
				System.out.println(docname);

				theFile = new FileInputStream(collectionZipFile);

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
						HTMLSimpleParser aux = new HTMLSimpleParser();
						String auxx = aux.parse(texto);
						String[] split = auxx.split(" ");
						for (int k = 0; k < split.length; k++) {
							for (String q : querys) {
								if (split[k].contains(q)) {
									for (int j = 0; j < MOSTRAR; j++) {
										if (split.length >= k + j) {
											System.out.print(split[k + j] + " ");
										}
									}
									System.out.println();
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
		Index aux = index;
		aux.loadReader();
		this.indice = aux.getReader();

	}

	@Override
	//Esta la normal, no literal
	public List<ScoredTextDocument> search(String query) {
		List<ScoredTextDocument> toret = new ArrayList();
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
			} else {
				double old = doctf_id.get(querys[0]);
				old += tf_idf;
				doctf_id.put(querys[0], old);
			}
		}

		String[] querysaux = new String[querys.length - 1];
		//quiero quitar el primer termino de la query
		for (int i = 0; i < querys.length - 1; i++) {
			querysaux[i] = querys[i + 1];
		}
		// vamos descartando los Postings que no cumplan la busqueda literal, los buenos se quedan en nuevaprincipal
		for (String qaux : querysaux) {
			List<Posting> termPostings = indice.getTermPostings(qaux);
			ArrayList<Posting> nuevaprincipal = new ArrayList<>();

			for (Posting postprincipal : termsprincipal) {
				for (Posting acomparar : termPostings) {
					List<Long> posicionesLiteral = postprincipal.posicionesLiteral(acomparar);
					if (postprincipal.getDocId().equals(acomparar.getDocId()) && posicionesLiteral.size() != 0) {

						Posting sobrevive = new Posting(postprincipal.getDocId(), posicionesLiteral);
						nuevaprincipal.add(sobrevive);
					}
				}
			}
			if (nuevaprincipal.size() == 0) {
				return null;
			}
			termsprincipal = nuevaprincipal;
			ArrayList<String> borrado = new ArrayList<>();
			//elimino del treemap todo lo que no este en termsprincipal, si no esta es que no es literal
			for (String key : doctf_id.keySet()) {
				int flag_borrado = 1;

				for (Posting post : termsprincipal) {
					if ((key.equals(post.getDocId()))) {
						flag_borrado = 0;
					}
				}
				if (flag_borrado == 1) {
					borrado.add(key);
				}
			}
			for (String borrarKey : borrado) {
				doctf_id.remove(borrarKey);
			}
			for (Posting post : termsprincipal) {
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
			double modulo = this.indice.getDiccionarioDocs_NM().get(docid).getModulo();
			old = old / modulo;
			//comprobar modulo correcto    
			doctf_id.put(docid, old);
		}
		int k = 0;

		for (int g = 0; g < termsprincipal.size(); g++) {
			String doc = termsprincipal.get(g).getDocId();
			double score = doctf_id.get(doc);
			ScoredTextDocument scoretext = new ScoredTextDocument(doc, score);
			toret.add(scoretext);
		}
		Collections.sort(toret);
		int topp = TOP;
		if (toret.size() < TOP) {
			topp = toret.size();
		}
		return toret.subList(0, topp);
	}

	/**
	 * Computa el producto de tf*id de un término y un documento dado.
	 *
	 * @param termino Termino.
	 * @param docid Id del documento.
	 * @return
	 */
	private double tf_idf(String termino, String docid) {

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
