/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

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
public class StopwordsParserTest {
	

	/**
	 * Test of parse method, of class StopwordsParser.
	 */
	@Test
	public void testParse() {
		System.out.println("parse");
		String text = "obama is a Family A IS Obama ";
		StopwordsParser instance = new StopwordsParser();
		String expResult = "obama Family A IS Obama ";
		String result = instance.parse(text);
		assertEquals(expResult, result);
	}
	
}
