/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author dani
 */
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
		
		for (int i = 1; i<l.size();i++){
			tofill[i-1] = Double.parseDouble(l.get(i));
		}
		return toret;
    }
       
    FilterCallableMovies(double[] tofill, String line){
		this.tofill = tofill;
		this.line = line;
	}
    
    
}



