/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import java.util.LinkedHashMap;
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
	
	@Override
	public Integer call() throws Exception {
		return line.length();
	}
	
}
