/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author victo
 */
public  class FilterCallableItem implements Callable<Integer> {
	
	public static int CODE = 0;
	
	double[] tofill;
	String line;
	
	FilterCallableItem(double[] tofill, String line){
		this.tofill = tofill;
		this.line = line;
	}
	/** El método de Test*/
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
