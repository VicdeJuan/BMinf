package es.uam.eps.bmi.recommend.FilterCallables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * ORIGEN DE LA CLASE:
 * El propósito de esta clase es tener un método modular que permita leer cualquier tipo de fichero con una estructura parecida. 
 * Esta estructura es:
 * 
 *	id1 propiedad1 propiedad2 propiedad 3
 *	id2 propiedad1 propiedad2 propiedad 3
 *	...
 *	idn propiedad1 propiedad2 propiedad 3
 * 
 * El objetivo es poder dar como argumento una función que dada una línea te devuelva un array de doubles (que será la fila 
 * de la matriz correspondiente) y el id del elemento que estamos procesando. La manera de hacer eso es tener clases de esta forma.
 * Cada función que procese líneas tendrá una clase asociada como esta.
 * 
 * 
 * EXPLICACIÓN DE LA CLASE:
 *	
 *	Para cada fichero que vayamos a leer en el sistema de recomendación necesitaremos definir un FilterCallableAsociado
 *	con el método para procesar el fichero y convertirlo en double[]. 
 *	Una vez creada la clase FilterCallableAsociada, tendremos que incluirla el método RecomenderAbs::CargarMatriz()
 *	para que se le llame cuando corresponda.
 * 
 *	CUIDADO con la definición de los CODE de cada FilterCallable. Tienen que ser unívocos.
 * 
 */
public  class FilterCallableItem implements Callable<Integer> {
	

	/**
	 * Código para poder, en el lector de ficheros, distinguir a qué FilterCallable hay que llamar.
	 */
	public static int CODE = 0;
	
	/**
	 * Definición de variables auxiliares. Estas variables son necesarias porque el método "call" de la interfaz
	 *  Callable (la que permite pasar métodos como argumentos) no permite pasar argumentos, asique damos este
	 * rodeo para conseguirlo.
	 */
	// Array en el que se almacenará la línea procesada y convertida en double[]-
	double[] tofill;
	// Línea a parsera.
	String line;
	
	// Constructor 
	public FilterCallableItem(double[] tofill, String line){
		this.tofill = tofill;
		this.line = line;
	}
	
	
	/** 
	 * El método de Test. 
	 * 
	 * Este es el método para procesar el fichero CRTItem.dat. En este caso es igual que el de FilterCallableUser,
	 * debido a la simpleza del ejemplo. A la hora de la verdad, el fichero movies.dat tiene muchas columnas que 
	 * deben ser ignoradas (título en español) y el fichero de usuarios tiene otras peculiaridades que tendrán que 
	 * ser tratadas como es debido en sus respectivos filterCallable.
	 */
	@Override
	public Integer call() throws Exception {
		List<String> l = Arrays.asList(line.split(" "));
		int toret = Integer.parseInt(l.get(0));
		
		for (int i = 1; i<l.size();i++){
			tofill[i-1] = Double.parseDouble(l.get(i));
		}
		return toret;
	}
	
}
