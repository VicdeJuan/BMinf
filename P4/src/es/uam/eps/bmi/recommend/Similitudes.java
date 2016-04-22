package es.uam.eps.bmi.recommend;

public class Similitudes {
	
	
	public static double coseno(double[] v1,double[] v2){
		
		if (v1.length != v2.length)
			return Double.NaN;
		double m1=0,m2=0,sum=0;
		boolean onenotnull = false;
		boolean twonotnull = false;

		
		
		
		for(int i = 0; i < v1.length;i++){
			if (v1[i] != 0)
				onenotnull = true;
			if (v2[i] != 0)
				twonotnull = true;
			m1 += Math.pow(v1[i],2);
			m2 += Math.pow(v2[i],2);
			sum += v1[i]*v2[i];			
		}
		if (!onenotnull || !twonotnull )
			return 0;
		return sum/(Math.sqrt(m1)*Math.sqrt(m2));
	}
	
	
}
