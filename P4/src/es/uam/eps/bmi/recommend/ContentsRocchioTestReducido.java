/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import org.ejml.data.DenseMatrix64F;

public class ContentsRocchioTestReducido {
	
    public static void main(String[] argv){
        
		System.out.println("rank");
		int user = 75;
		int item = 3;
		double[] d ={1,2,3,4,1,2,3,4,1,2,3,4};
		DenseMatrix64F m = new DenseMatrix64F(3,4);
		m.setData(d);
		System.out.println(m);
		
		DenseMatrix64F m2  = new DenseMatrix64F(4,5);
			
		m2.set(m);
		System.out.println(m2);
		

        
        
		//assertEquals(expResult, result, 0.01);
	}
	
	/**
	 * Test of rank method, of class ContentsRocchio.
	 */
    /*
	@Test
	public void testRank() {
		System.out.println("rank");
		int user = 1;
		int item = 2;
		
		ContentsRocchio instance = new ContentsRocchio("data/CRTItem.dat", "data/CRTUser.dat");
		
		// La traspa dice 0.86 pero yo diría que está bien así.
		double expResult = 0.887;
		double result = instance.rank(user, item);
		assertEquals(expResult, result, 0.01);
		
	}
*/

}
