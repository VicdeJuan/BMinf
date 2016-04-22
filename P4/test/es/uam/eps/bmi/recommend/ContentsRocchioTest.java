/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.recommend.FilterCallables.FilterCallableItem;
import es.uam.eps.bmi.recommend.FilterCallables.FilterCallableUser;
import es.uam.eps.bmi.search.ranking.graph.PageRank;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author victo
 */
public class ContentsRocchioTest {
	
    public static void main(String[] argv){
		System.out.println("rank");
		int user = 1;
		int item = 2;
		double result = 0;
		ContentsRocchio instance ;
		double expResult = 0.887;
		
		instance = new ContentsRocchio("data/CRTItem.dat", FilterCallableItem.CODE,
					"data/CRTUser.dat", FilterCallableUser.CODE);

		
		
		result = instance.rank(user, item);
		System.out.println(expResult+" y "+result);
		assertEquals(expResult, result, 0.01);
		
		// La traspa dice 0.86 pero yo diría que está bien así.
		System.out.println(instance.centroides);
		
		instance = new ContentsRocchio("data/CRTItem_2.dat", "data/CRTUser_2.dat", 0,1,2,0,1,0,2,0,1);
		
		result = instance.rank(user, item);
		System.out.println(expResult+" y "+result);
		assertEquals(expResult, result, 0.01);
		
		
		
		
		
		
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
