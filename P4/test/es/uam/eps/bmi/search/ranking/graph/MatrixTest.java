package es.uam.eps.bmi.search.ranking.graph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatrixTest {
	
	

	/**
	 * Test of addRowLast method, of class Matrix.
	 */
	@Test
	public void testAddRowLast() {
		System.out.println("addRowLast");
		
		double [] initial = {1,2,3,4};
		Matrix instance = new Matrix(initial, 2, 2);
		instance.addRowLast(2);
		double[] expected = {1,2,3,4,0,0,0,0};
		System.out.print(instance);
		for (int i=0; i < expected.length; i++)
			assertEquals(expected[i], instance.getData()[i],0.000001);
	}

	/**
	 * Test of addColLast method, of class Matrix.
	 */
	@Test
	public void testAddColLast() {
		double [] initial = {1,2,3,4};
		Matrix instance = new Matrix(initial, 2, 2);
		instance.addColLast(2);
		double[] expected = {1,2,0,0,3,4,0,0};
		System.out.print(instance);

		for (int i=0; i < expected.length; i++)
			assertEquals(expected[i], instance.getData()[i],0.000001);
	}

	
	
}
