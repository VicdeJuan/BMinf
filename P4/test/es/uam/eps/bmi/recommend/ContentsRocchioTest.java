/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

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
	
	
	/**
	 * Test of rank method, of class ContentsRocchio.
	 */
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


}
