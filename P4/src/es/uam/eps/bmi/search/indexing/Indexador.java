/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;
import sun.misc.IOUtils;

/**
 *
 * @author parra
 */

public class Indexador {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        
        //InputStream theFile = new FileInputStream("/home/parra/Escritorio/p3Bminf/pruebas/pruebas.zip");
        //ZipInputStream stream = new ZipInputStream(theFile);
        //String inpath = "/home/parra/Escritorio/p3Bminf/clueweb-1K/";
        //String outpath = "/home/parra/Escritorio/p3Bminf/clueweb-1K-new/";
        String inpath = "colecciones/clueweb-1K/";
        String outpath = "colecciones/clueweb-1K/";
        
        BasicIndex index = new BasicIndex();
        index.build(inpath, outpath, new HTMLSimpleParser());
        
        
    }
}

