package es.uam.eps.bmi.search.ranking.graph;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import es.uam.eps.bmi.search.Utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import org.ejml.ops.CommonOps;

public class PageRank {

	private String fileOfLinks;
	private Matrix matrix;
	private double[] scores;
	private int numDocs;
	private LinkedHashMap<String, Integer> dicNameDocID;
	private int countDocs; // Variable para crear la matriz del grafo.
	
	private final int MAX_ITER = 100;
	
	public PageRank(String fileOfLinks) {
		int collectionSize = 0;
		// Calcular tamañaoI

	}

	public PageRank(String fileOfLinks, int collectionSize) {
		
		//int nrow;
		//nrow = 1;
		dicNameDocID = new LinkedHashMap<>();
		matrix = new Matrix( collectionSize, collectionSize);
		load(fileOfLinks,collectionSize);
		scores = new double[collectionSize];
		
	
	}
	
	public Matrix iterate(double precission,int numIterations){
		return iterate(numIterations);
	}
	public Matrix iterate(double precission){
		
		return iterate(precission,MAX_ITER);
	}
	public Matrix iterate(int numIterations){
		Matrix m = matrix.power(matrix,numIterations);
		return m;
		//return m.getRow(0);
	}

	private void load(String fileOfLinks, int collectionSize) {
		FileReader fr = null;
		int row = 0, col;
		String doc;
		countDocs = 0;
		try {
			fr = new FileReader(fileOfLinks);

			BufferedReader br = new BufferedReader(fr);

			String line;

			while ((line = br.readLine()) != null) {
				String[] _links = line.split(Utils.ExternPostingSeparator);
				doc = _links[0];

				if (!dicNameDocID.containsKey(doc)) {
					row = countDocs;
					dicNameDocID.put(doc, countDocs++);
				} else {
					row = dicNameDocID.get(doc);
				}

				if (_links.length <= 2) {
					// Fijamos a 0 toda la fila correspondiente a este documento.
					//matrix.setRow(0,row);
					// No hace falta porque la matriz ya está a 0 entera por defecto.					
					continue;
				}
				List<String> links = Arrays.asList(Arrays.copyOfRange(_links, 2, _links.length));
				//matrix.setRow(0,row);
				for (String l : links) {
					// Comprobamos que documento esté en el diccionario
					if (!dicNameDocID.containsKey(l)) {
						col = countDocs;
						dicNameDocID.put(l, countDocs++);
					} else {
						col = (int) dicNameDocID.get(l);
					}
					// Ponemos un 1 en el enlace correspondiente.
					matrix.set(1.0, row, col);
				}

			}
			matrix.normalize();
			
			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("No se ha encontrado el archivo " + fileOfLinks);
		} catch (IOException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public double getScoreOf(String docId) {
		return matrix.get((int) dicNameDocID.get(docId),0);
	}


	@Override
	public String toString() {
		return this.matrix.toString();
	}

}
