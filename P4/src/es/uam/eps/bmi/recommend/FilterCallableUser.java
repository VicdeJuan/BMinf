package es.uam.eps.bmi.recommend;

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
public class FilterCallableUser implements Callable<Integer> {
	double[] tofill;
	String line;
	
	public static int CODE = 1;

	public FilterCallableUser(double[] t, String l) {
		tofill = t;
		line = l;
	}
	
	
	/** Método de test */
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
