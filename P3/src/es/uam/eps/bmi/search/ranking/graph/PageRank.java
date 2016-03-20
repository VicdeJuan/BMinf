package es.uam.eps.bmi.search.ranking.graph;

import es.uam.eps.bmi.search.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.apache.commons.collections4.comparators.ComparableComparator;

public class PageRank {

	public double DEFAULT_PRECISSION = 0.00005;

	public final String filetowrite = "colecciones/pageRank/page_rank1K.txt";

	private String fileOfLinks;
	private Matrix matrix;
	private double[] scores;
	private int numDocs;
	private LinkedHashMap<String, Integer> dicNameDocID;
	private int countDocs; // Variable para crear la matriz del grafo.
	private double r; // r entre 0 y 1. Ponderación del peso de la probabilidad de teleportación.
	private boolean scoresCalculated = false;

	private final int MAX_ITER = 100;

	public void writeValues() {
		int lim = 10;
		
		List<String> docs = dicNameDocID.entrySet().stream().sorted(
			(
				Map.Entry<String, Integer> o1,
				Map.Entry<String, Integer> o2)
			-> Double.compare(scores[o2.getValue()], scores[o1.getValue()])
		).limit(lim).map(
			(ent) -> ent.getKey()
		).collect(Collectors.toList());

		try {
			FileWriter fw = new FileWriter(filetowrite);
			BufferedWriter bw = new BufferedWriter(fw);

			if (!scoresCalculated) {
				calculateScores();
			}

			for (String d : docs) {
				bw.write(d + " " + scores[dicNameDocID.get(d)] + "\r\n");
				bw.write(getFirstLines(d));
			}
			bw.close();

		} catch (IOException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public PageRank(String fileOfLinks, int collectionSize, double r) {

		//int nrow;
		//nrow = 1;
		dicNameDocID = new LinkedHashMap<>();
		matrix = new Matrix(collectionSize, collectionSize);
		this.r = r;
		load(fileOfLinks, collectionSize);
		scores = new double[collectionSize];

	}

	private Matrix _iterate(Matrix matrix, double precission, int numIterations) {
		Matrix m = Matrix.power(matrix, 2);
		Matrix sc = new Matrix(scores, m.getNumRows(), 1);

		if (numIterations <= 2) {
			return m;
		}

		OptionalDouble od = DoubleStream.of(
			Matrix.resta(
				m,
				matrix).
			getData()).max();

		if (od.isPresent()) {
			if (od.getAsDouble() < precission) {
				return m;
			} else {

				return _iterate(m, precission, numIterations - 1);
			}
		}

		return _iterate(m, precission, numIterations);
	}

	public Matrix iterate(double precission, int numIterations) {
		return _iterate(matrix, precission, numIterations);

	}

	public Matrix iterate(double precission) {

		return iterate(precission, MAX_ITER);
	}

	public Matrix iterate(int numIterations) {
		Matrix m = Matrix.power(matrix, numIterations);
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
			// Normalizamos la matriz
			matrix.normalize();
			//Añadimos la correspondiente matriz de 1/N
			matrix.teleport(this.r);

			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("No se ha encontrado el archivo " + fileOfLinks);
		} catch (IOException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void calculateScores() {
		scoresCalculated = true;
		scores = iterate(DEFAULT_PRECISSION).getRow(0);
	}

	public double[] getScores() {
		return scores;
	}

	public double getScoreOf(String docId) {
		return matrix.get((int) dicNameDocID.get(docId), 0);
	}

	@Override
	public String toString() {
		return this.matrix.toString();
	}

	private char[] getFirstLines(String d) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
