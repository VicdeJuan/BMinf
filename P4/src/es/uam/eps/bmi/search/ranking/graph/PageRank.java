package es.uam.eps.bmi.search.ranking.graph;

import es.uam.eps.bmi.search.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class PageRank {

	public double DEFAULT_PRECISSION = 0.00005;

	public String filetowrite;
	public String colection; 

	private String fileOfLinks;
	private final Matrix matrix;
	private double[] scores;
	private int numDocs;
	private final LinkedHashMap<String, Integer> dicNameDocID;
	private int countDocs; // Variable para crear la matriz del grafo.
	private final double r; // r entre 0 y 1. Ponderación del peso de la probabilidad de teleportación.
	private boolean scoresCalculated = false;

	private final int MAX_ITER = 100;

	public PageRank(String fileOfLinks, int collectionSize, double r, String fileToWrite, String colection) {

		//int nrow;
		//nrow = 1;
		dicNameDocID = new LinkedHashMap<>();
		matrix = new Matrix(collectionSize, collectionSize);
		this.r = r;
		load(fileOfLinks, collectionSize);
		scores = new double[collectionSize];
		this.colection = colection;
		filetowrite = fileToWrite;

	}
	
	public PageRank(String fileOfLinks,int collectionSize,double r){
		this(fileOfLinks,collectionSize,r,null,null);
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

		HashMap<String, String> dicDocFirstLines;
		try {
			FileWriter fw = new FileWriter(filetowrite);
			BufferedWriter bw = new BufferedWriter(fw);

			if (!scoresCalculated) {
				calculateScores();
			}

			dicDocFirstLines = getFirstLines(docs);

			for (String d : docs) {
				bw.write(d + " " + scores[dicNameDocID.get(d)] + "\r\n");
				bw.write(dicDocFirstLines.get(d) + "\r\n");
			}
			bw.close();

		} catch (IOException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	
	private HashMap<String, String> getFirstLines(List<String> docs) {

		HashMap<String, String> toret = new HashMap<>();
		FileInputStream theFile;
		int read;
		String re,name;
		byte[] buffer = new byte[2048*5];
		try {
			theFile = new FileInputStream(this.colection);

			ZipInputStream stream = new ZipInputStream(theFile);
			ZipEntry entry;
			
			
			while ((entry = stream.getNextEntry()) != null) {
				// Eliminamos la extensión del nombre
				name = entry.getName().substring(0, entry.getName().length()-5);
				if (docs.indexOf(name) != -1) {
					read = stream.read(buffer);
					toret.put(name,new String(buffer, 0, (int) read, "UTF-8"));
				}
			}
		} catch (FileNotFoundException ex) {
			System.err.println("No se encuentra el archivo para mostrar el contenido de cada documento");
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			System.err.println("No se encuentra el archivo para mostrar el contenido de cada documento");

			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
			return null;

		}
		return toret;
	}
}
