package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.StemIndex;
import es.uam.eps.bmi.search.indexing.StopwordIndex;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.XMLReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearcherTest {

	public static void _evaluate_results(String outputFile, ArrayList<ArrayList<String>> resultados, int max, String compareToFile) throws IOException {

		final PrintWriter pwout = new PrintWriter(new FileWriter(outputFile));
		BufferedReader expected_file = new BufferedReader(new FileReader(compareToFile));
		Integer[] encontrados = new Integer[resultados.size() + 1];
		for (int x = 0; x < encontrados.length; x++) {
			encontrados[x] = 0;
		}
		int j = 0;
		String cadena;
		while ((cadena = expected_file.readLine()) != null) {

			String[] split = cadena.split("\t");
			List<String> keys = new ArrayList<>(Arrays.asList(split));
			for (int l = 0; l < Math.min(max, resultados.get(j).size()); l++) {

				//vemos si cada elemento de la query esta en relevance
				for (String key : keys) {
					if (key.equals(resultados.get(j).get(l))) {
						encontrados[j]++;
						break;
					}
				}

			}
			pwout.println("Query: " + (j + 1) + "\t" + encontrados[j] * 1.0 / max);

			j++;
		}
		pwout.close();
	}

	/**
	 * Construye el fichero oportuno a partir de las busquedas segun los
	 * argumentos aportados
	 *
	 * @param outputFile Fichero en el que almacenar la informacion
	 * @param LucIdx Indice de Lucene.
	 * @param queryFile Fichero con las consultas.
	 * @param max Numero maximo de resultados.
	 * @return La lista de los documentos que corresponden a la query.
	 * @throws IOException
	 */
	private static ArrayList<ArrayList<String>> _build_results(String outputFile, Index LucIdx, Searcher lucSearch, String queryFile, int max) throws IOException {

		final PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
		ArrayList<ArrayList<String>> resultados = new ArrayList<>();
		String docName = "";
		if (LucIdx.getReader() != null) {

			lucSearch.build(LucIdx);

			int indice = 1;

			pw.println(" querys Top " + max);

			for (String query : Files.readAllLines(Paths.get(queryFile))) {
				List<ScoredTextDocument> resul = lucSearch.search(query.substring(query.indexOf(":") + 1));
				pw.println(query + " -- " + (indice++) + ":");

				if (resul != null && resul.size() > 0) {
					ArrayList<String> aux = new ArrayList<>();
					String toadd;
					for (int i = 0; i < Math.min(max, resul.size()); i++) {
						docName = lucSearch.getDocName(resul.get(i).getDocId());
						toadd = docName.substring(0, docName.lastIndexOf("."));
						aux.add(toadd);
						pw.write(toadd + "\t");
					}
					resultados.add(aux);
				} else {
					ArrayList<String> aux = new ArrayList<>();
					aux.add("Query Vacia");
					resultados.add(aux);
					pw.write("Query vacia\t");
				}
				pw.write("\n");
			}

			pw.close();
		}
		return resultados;
	}

	public static void main(String[] argv) throws IOException {

		XMLReader xmlReader = new XMLReader("index-settings.xml");
		String indexDir = xmlReader.getTextValue(Utils.XMLTAG_INDEXFOLDER);
		String collectionPath = xmlReader.getTextValue(Utils.XMLTAG_COLLECTIONFOLDER);
		String collectionZipFile = collectionPath;

		boolean small = false;

		ArrayList<String> sizes = new ArrayList<>();
		sizes.add("1K");
		if (!small) {

			sizes.add("10K");
			sizes.add("100K");
		}

		String collectionFolder = "colecciones/clueweb-";
		String indexFolder = "indices/";

		HashMap<String, Index> indices = new HashMap<>();
		indices.put("basic", (Index) new BasicIndex());
		if (!small) {
			indices.put("stem", (Index) new StemIndex());
			indices.put("stopword", (Index) new StopwordIndex());
		}

		ArrayList<Integer> maxs = new ArrayList<>();
		maxs.add(5);
		if (!small) {
			maxs.add(10);
		}

		ArrayList<Searcher> searchers = new ArrayList<>();
		searchers.add(new TFIDFSearcher());
		if (!small) {
			searchers.add(new LiteralMatchingSearcher());
		}

		Runtime runtime = Runtime.getRuntime();
		Index idx;
		
		
		
		for (String size : sizes) {

			for (Map.Entry<String, Index> e : indices.entrySet()) {
				System.out.println("" + e.getKey() + ":");
				idx = e.getValue();

				File dir = new File(indexFolder + size + "/" + e.getKey());
				boolean c = dir.mkdirs();
				// Stop y Stem ignoran en tercer argumento.
				long allocatedMemory = runtime.totalMemory();
				long startTime = System.currentTimeMillis();
				idx.build(collectionFolder + size+"/", dir.getPath() , new HTMLSimpleParser());
				
				System.out.print(dir.getPath());
				long estimatedTime = System.currentTimeMillis() - startTime;
				long estimatedAllocatedMemory = runtime.totalMemory() - allocatedMemory;
				long diskSize = new File(dir.getPath() + Utils.index_file).length();
				System.out.println("Creado " + e.getKey() + "index");

				// Búsqueda
				String indexPath = "outputCollection_" + size;
				long estimatedAllocatedMemory_sch = 0,estimatedTime_sch=0; 
				for (Searcher sch : searchers) {
					BufferedWriter bw = new BufferedWriter(new FileWriter("timing_"+e.getKey()+"-"+sch.toString()));
					for (int max : maxs) {
						System.out.println("\t" + max);
						String outputFile_build = "Querys" + "_" + max + "-" + size + "-" + sch.toString() + "-" + e.getKey() + ".txt";
						String outputFile_evaluate = "Comparacion" + "_" + max + "-" + size + "-" + sch.toString() + "-" + e.getKey() + ".txt";
						String queryFile = collectionFolder + size + "/queries.txt";
						String compareToFile = collectionFolder + size + "/relevance.txt";

						idx.load(dir.getPath()+"/");
						//Calculamos resultados
						long allocatedMemory_sch = runtime.totalMemory();
						long startTime_sch = System.currentTimeMillis();
						ArrayList<ArrayList<String>> resultados = _build_results(outputFile_build, idx, sch, queryFile, max);
						estimatedTime_sch += System.currentTimeMillis() - startTime;
						estimatedAllocatedMemory_sch += runtime.totalMemory() - allocatedMemory;

						//evaluamos los resultados
						_evaluate_results(outputFile_evaluate, resultados, max, compareToFile);

					}
					bw.write(String.format("%s&%d&%d&%d&%d&%d\\\\",size+"_"+sch.toString()+"_"+e.getKey(),estimatedTime,estimatedAllocatedMemory,diskSize,estimatedTime_sch,estimatedAllocatedMemory_sch));
				}

				
			}
		}
	}

}
