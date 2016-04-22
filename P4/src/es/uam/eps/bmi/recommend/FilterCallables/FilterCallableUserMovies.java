package es.uam.eps.bmi.recommend.FilterCallables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;


public class FilterCallableUserMovies implements Callable<Integer> {
    double[] tofill;
	String line;
	
	public static int CODE = 3;

	public FilterCallableUserMovies(double[] t, String l) {
		tofill = t;
		line = l;
	}
	
	
	/** Método de test */
	@Override
	public Integer call() throws Exception {
		List<String> l = Arrays.asList(line.split("\t"));
		int toret = Integer.parseInt(l.get(0));

		for (int i = 1; i<3;i++){
			tofill[i-1] = Double.parseDouble(l.get(i));
		}
		return toret;
	}
	
	
	
}