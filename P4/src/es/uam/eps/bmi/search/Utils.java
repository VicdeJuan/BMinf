package es.uam.eps.bmi.search;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Integer.sum;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

public class Utils {

    	public static int getSizeOfFile(String file) {
		String line;
		int size = 0;
		TreeSet<String> bag = new TreeSet<>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				String[] s = line.split(" ");
				bag.add(s[0]);
				
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return bag.size();
	}
	
	public static double coseno(double[] v1,double[] v2){
		
		if (v1.length != v2.length)
			return Double.NaN;
		double m1=0,m2=0,sum=0;
		for(int i = 0; i < v1.length;i++){
			m1 += Math.pow(v1[i],2);
			m2 += Math.pow(v2[i],2);
			sum += v1[i]*v2[i];			
		}
		return sum/(Math.sqrt(m1)*Math.sqrt(m2));
	}

}

    

