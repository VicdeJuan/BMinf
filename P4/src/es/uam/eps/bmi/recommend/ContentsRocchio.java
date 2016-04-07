package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.util.LinkedHashMap;


public class ContentsRocchio extends RecommenderAbs {
	
	protected Matrix tagsItemsMatrix;
	protected Matrix centroides;
	
	protected int numTags;
	
	/**
	 *Dado que los id's en el fichero pueden no estar ordenados de 1 a n (como
	 *	es el caso de movies.dat que empiezan en 75), necesitamos almacenar que el id 75 
	 *	se corresponde con la primera columna de la matriz. Para esto están estos LinkedHashMap.
	 */
	LinkedHashMap<Integer,Integer> IdtoIdx_user;
	LinkedHashMap<Integer,Integer> IdtoIdx_items;
	
	public void setIdtoIdxUser(LinkedHashMap<Integer,Integer> ididxuser){
		IdtoIdx_user = ididxuser;
	}	
	public void setIdtoIdxItem(LinkedHashMap<Integer,Integer> ididxitem){
		IdtoIdx_items = ididxitem;
	}

	
	public ContentsRocchio(String fileofContents,String fileOfUsers){
		// Obtenemos las variables previas necesarias.
		numUser = Utils.getSizeOfFile(fileOfUsers);
		numItem = Utils.getSizeOfFile(fileofContents);
		// TODO: este 4 está puesto a pelo para satisfacer el ejemplo. 
		//	Hay que definir un Utils.getColumnsOfFile() para que funcione en todos los casos.
		numTags = 4;
		
		// Inicialización de variables utilizadas en los métodos load.
		IdtoIdx_items = new LinkedHashMap<>();
		IdtoIdx_user = new LinkedHashMap<>();
		// Cargamos matriz de items.
		loadContents(fileofContents);
		
		// Cargamos matriz de usuarios.
		loadUserMatrix(fileOfUsers);
		_calculateCentroides();
	}
	
	private void _calculateCentroides(){
		centroides = Matrix.producto(tagsItemsMatrix, matriz.transpose());
		centroides = centroides.RocchiNormalize(matriz.transpose());
		
	}
	
	/**
	 *  LOADS:
	 * 
	 *	Ambos métodos son muy parecidos. 
	 * 
	 *	La matriz a rellenar es una variable de la clase y cada método rellena la correspondiente. 
	 *	Lo mismo con el diccionario que mantiene la correspondencia entre id's reales e índices de matrices.
	 *	El CODE de cada método corresponde al FilterCallable con el que se debe procesar cada fichero.
	 *	El último arg. indica si cada fila del fichero corresponde a una columna de la matriz o no.
	 */
	
	/**
	 * Carga el contenido del fichero en las variables de la clase correspondientes.
	 * @param fileOfContents	fichero a leer.
	 */
	public final void loadContents(String fileOfContents){
		tagsItemsMatrix = new Matrix(numTags, numItem);
 		super.CargarMatriz(fileOfContents, tagsItemsMatrix, FilterCallableItem.CODE ,IdtoIdx_items,true);
	}
	
	/**
	 * Carga el contenido del fichero en las variables de la clase correspondientes.
	 * @param fileOfUsers		fichero a leer.
	 */
	public final void loadUserMatrix(String fileOfUsers){
		matriz = new Matrix(numUser,numItem);
		super.CargarMatriz(fileOfUsers, matriz, FilterCallableUser.CODE, IdtoIdx_user,false);
	}
	

	/**
	 * Ranking de Rochio. Utiliza la función de similitud coseno.
	 * @param user
	 * @param item
	 * @return 
	 */
	@Override
	public double rank(int user, int item) {
		int idxuser = IdtoIdx_user.get(user);
		int idxitem = IdtoIdx_items.get(item);
		double[]v1 = centroides.getCol(idxuser);
		double[] v2= tagsItemsMatrix.getCol(idxitem);
		return Similitudes.coseno(v1,v2);
	}
	
}
