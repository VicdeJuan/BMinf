package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.ranking.graph.Matrix;
import es.uam.eps.bmi.search.ranking.graph.PageRank;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase abstracta necesaria para que quien herede de recomender tenga los mismos métodos. 
 * Así evitamos repetición de código.
 * 
 * Si Java dejara implementar métodos en una interfaz, esto no haría falta. Pero Java no lo permite.
 * 
 */
public abstract class RecommenderAbs implements Recommender {
	/**
	 * Matriz del recomendador. Típicamente será con los usuarios como filas y los items como columnas
	 */
	public Matrix matriz = null;
	int numUser =0;
	int numItem =0;
	
	
	
	
	/**
	 *  Lee el fichero y construye la matriz. Este método tiene bastante enjundia para poder ser utilizado por todos
	 * los recomendadores que hagamos. 
	 * 
	 *  Por otro lado, cada fichero tiene una estructura distinta, distintos campos, etc... Por eso, según el tipo de fichero que sea
	 *  (determinado por flagFileType) se llamará a un parser u otro. Estos parsers son las clases FilterCallable*.java.
	 * 
	 * 
	 * 
	 * @param fichero	Fichero de donde leer la información.
	 * @param mat	Matriz a rellenar de datos. Hay que pasarla creada pero vacía.
	 * @param flagFileType Tipo de fichero a procesar. Cada tipo tiene asociado un valor en su correspondiente FilterCallable.
	 *		Para más información, consultar FilterCallableItem.java.
	 *				
	 * @param RealIDtoIdx	Dado que los id's en el fichero pueden no estar ordenados de 1 a n (como
			 es el caso de movies.dat que empiezan en 75), necesitamos almacenar que el id 75 
	 *		se corresponde con la primera columna de la matriz. Para esto está el LinkedHashMap.
	 * 
	 * @param fillCols	El fichero puede interesar leerlo como matriz normal o como matriz traspuesta. Para entender esto, 
	 *		 fijarse en el ejemplo de las transparencias de Rocchio y en los ficheros 
	 *		CRTItem.dat y CRTUser.dat. En las transparencias el la matriz de usuarios 
	 *		tiene los usuarios como filas y el fichero CRTUser.dat tiene los usuarios 
	 *		como filas, entonces  vamos a rellenar la matriz fila a fila, con lo que 
	 *		el argumento fillCols es false. Por el contrario, el fichero CRTItem.dat 
	*		tiene los items en las filas, pero nos interesa tenerlos en las columnas, 
	*		como se ve en el ejemplo. Por ello, rellenamos  la matriz por columnas, 
	*		es decir, el argumento fillCols es True. 
	*		Para más información, ver las llamadas en  ContentsRocchioTest.java. 
	 */
	public void CargarMatriz(String fichero,Matrix mat, int flagFileType, LinkedHashMap<Integer,Integer> RealIDtoIdx, boolean fillCols){
		
		
		if (numItem == 0 || numUser == 0){
			System.err.println("Tenemos un problema si el número de items es 0. Hay que setear su valor con anterioridad");
			System.exit(-1);
		}
		// Inicialización de variables		
		FileReader fr = null;
		int row = numUser, col = numItem,counter = 0;
		String doc;
		double[] tofill = null;
		
		try {
			fr = new FileReader(fichero);

			BufferedReader br = new BufferedReader(fr);

			String line;
			
			/**
			 * Este bucle lee el fichero linea a linea. Para cada linea, la procesa y rellena la matriz 
			 *	que hemos dado como argumento. Procesa la línea según lo especificado
			 *	por argumento por flagFileType, llamando a un filterCallable o a otro.
			 */
                        int paso=0;
			while ((line = br.readLine()) != null) {
				
				// Creación del vector a ser rellenado por FilterCallable*.java
				tofill = new double[mat.getNumCols()];
				// Id del elemento que procesamos en la línea.
				int id;
				//metemos una flag para comernos la cabecera
                                boolean flag=true;
                                
                                
				// Añadir el resto de posibles filtros dependiendo del fichero
				if (flagFileType == FilterCallableMovies.CODE){
                                        
                                        if(paso>0){
					id = (new FilterCallableMovies(tofill, line)).call();
                                        }
                                        else{ paso++;
                                            continue;}
                                }
                                else if(flagFileType == FilterCallableUser.CODE)
					id = (new FilterCallableUser(tofill, line)).call();
                                else if(flagFileType == FilterCallableItem.CODE)
					id = (new FilterCallableItem(tofill, line)).call();
                                else{
                                        if(paso>0){
                                        id = (new FilterCallableUserMovies(tofill, line)).call();
                                        }
                                        else{ paso++;
                                            continue;}
                                }
				
				// Para llevar registro en caso de no tener todos los ids en el fichero.
				RealIDtoIdx.putIfAbsent(id, counter);
				paso++;
				// Si rellenamos la matriz por columnas o por filas.
				if (fillCols)
					mat.setCol(tofill,counter);
				else
					mat.setRow(tofill, counter);
				counter++;
			}
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

	/**
	 * Setters & getters
	 */
	public void setNumUser(int numUser){
		this.numUser = numUser;
	}
	public void setNumItem(Integer numItem){
		this.numItem = numItem;
	}
}
