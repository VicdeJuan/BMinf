/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import es.uam.eps.bmi.search.ranking.graph.PageRank;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victo
 */
public abstract class RecommenderAbs implements Recommender {
	/**
	 *
	 */
	public Matrix matriz = null;

	/**Filas usuarios/tags, columnas items
	 *
	 */
	int numUser =0;
	int numItem =0;
	
	

	public void setNumUser(int numUser){
		this.numUser = numUser;
	}
	public void setNumItem(Integer numItem){
		this.numItem = numItem;
	}
	
	/**
	 *
	 * @param fichero	Fichero de donde leer la información.
	 * @param mat	Matriz a rellenar de datos. La crea de la nada.
	 * @param flagFileType Tipo de fichero a procesar. Cada tipo tiene asociado un valor en su correspondiente FilterCallable.
	 * @param RealIDtoIdx
	 */
	public void CargarMatriz(String fichero,Matrix mat, int flagFileType, LinkedHashMap<Integer,Integer> RealIDtoIdx, boolean fillCols){
		
		
		if (numItem == 0 || numUser == 0){
			System.err.println("Tenemos un problema si el número de items es 0. Hay que setear su valor con anterioridad");
			System.exit(-1);
		}
		
			
			
		
		
		FileReader fr = null;
		int row = numUser, col = numItem,counter = 0;
		String doc;
		double[] tofill = null;
		
		try {
			fr = new FileReader(fichero);

			BufferedReader br = new BufferedReader(fr);

			String line;

			while ((line = br.readLine()) != null) {
				tofill = new double[mat.getNumCols()];
				int id;
					
				// Añadir el resto de posibles filtros dependiendo del fichero
				if (flagFileType == FilterCallableItem.CODE)
					id = (new FilterCallableItem(tofill, line)).call();
				else
					id = (new FilterCallableUser(tofill, line)).call();
				RealIDtoIdx.putIfAbsent(id, counter);
				if (fillCols)
					mat.setCol(tofill,counter);
				else
					mat.setRow(tofill, counter);
				counter++;
			}
			// Normalizamos la matriz
			//mat.normalize();
			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("No se ha encontrado el archivo " + fichero);
		} catch (IOException ex) {
			Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(RecommenderAbs.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
