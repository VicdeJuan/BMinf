package es.uam.eps.bmi.recommend.FilterCallables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class FilterCallableMovies implements Callable<Integer> {

	public static int CODE = 2;
	// Array en el que se almacenará la línea procesada y convertida en double[]
	double[] tofill;
	// Línea a parsear.
	String line;

	@Override
	public Integer call() throws Exception {
		List<String> l = Arrays.asList(line.split("\t"));
		int toret = Integer.parseInt(l.get(0));

		for (int i = 1; i < l.size(); i++) {
			tofill[i - 1] = Double.parseDouble(l.get(i));
		}
		return toret;
	}

	public FilterCallableMovies(double[] tofill, String line) {
		this.tofill = tofill;
		this.line = line;
	}

}
