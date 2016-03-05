/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vicdejuan
 */
public class BasicIndexTest {
    
    public BasicIndexTest() {
    }
    
    String inputCollectionPath = "pruebas/docs.zip";
    String outputIndexPath = "pruebas/docIndice_obtained";
    TextParser textParser = new HTMLSimpleParser();
        
    
    /**
     * Test of build method, of class BasicIndex.
     * @throws java.io.IOException
     */
    @Test
    public void testBuild() throws IOException {
        System.out.println("build");
        BasicIndex instance = new BasicIndex();
        instance.build(inputCollectionPath, outputIndexPath, textParser);
        String expected = Arrays.toString(Files.readAllBytes(Paths.get("pruebas/docIndice1")));
        String obtained = Arrays.toString(Files.readAllBytes(Paths.get(outputIndexPath)));
        assertEquals(expected, obtained);
    }

    
    /**
     * Test of stringToPosting method, of class BasicIndex.
     */
    @Test
    public void testStringToPosting() {
        System.out.println("stringToPosting");
        String str = "";
        Posting expResult = null;
        //Posting result = BasicIndex.stringToPosting(str);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of load method, of class BasicIndex.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        BasicIndex instance = new BasicIndex();
        instance.build(inputCollectionPath, outputIndexPath, textParser);
        
        instance.load(outputIndexPath);
        
        
    }

    
    /**
     * Test of getDocIds method, of class BasicIndex.
     */
    @Test
    public void testGetDocIds() {
        System.out.println("getDocIds");
        BasicIndex instance = new BasicIndex();
        List<String> expResult = new ArrayList<>();
        for(int i = 1; i <= 4; ++i){
            expResult.add(""+i);
        }
        instance.load(outputIndexPath);
        List<String> result = instance.getDocIds();
        assertEquals(expResult, result);
        
        
    }

    /**
     * Test of getTerms method, of class BasicIndex.
     */
    @Test
    public void testGetTerms() {
        System.out.println("getTerms");
        BasicIndex instance = new BasicIndex();
        List<String> expResult = new ArrayList<>();
        expResult.add("a");expResult.add("b");expResult.add("c");expResult.add("d");
        expResult.add("e");expResult.add("f");expResult.add("g");expResult.add("h");
        expResult.add("i");expResult.add("j");expResult.add("k");expResult.add("l");
        expResult.add("m");expResult.add("n");expResult.add("ñ");expResult.add("o");
        expResult.add("pq");expResult.add("r");expResult.add("s");expResult.add("t");
        expResult.add("u");expResult.add("v");expResult.add("w");
        expResult.add("x");expResult.add("y");expResult.add("z");
        instance.load(outputIndexPath);
        List<String> result = instance.getTerms();
        assertEquals(expResult, result);
        
    }

 
    
}
