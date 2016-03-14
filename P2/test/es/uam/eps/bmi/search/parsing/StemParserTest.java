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
public class StemParserTest {
	

	/**
	 * Test of parse method, of class StemParser.
	 */
	@Test
	public void testParse() {
		System.out.println("parse");
		String text = "Obama obama OBAMA traditional expected information presidency presiding happiness happily discouragement battles";
		StemParser instance = new StemParser();
		String expResult = "obama obama obama tradition expect inform presid presid happi happili discourag battl ";
		String result = instance.parse(text);
		assertEquals(expResult, result);
	}
	
}
