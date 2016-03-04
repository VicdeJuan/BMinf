/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

/**
 *
 * @author parra
 */
public class Pruebas2 {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        String input = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/pruebas.zip";
        String outpath = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docIndicePrueba1";
        HTMLSimpleParser parser = new HTMLSimpleParser(); //QUita del text html todas las etiquetas.
        BasicIndex index = new BasicIndex();
        
        //Para probarlo, ejecutar primero index.build, y despues, comentar esa linea para futuras pruebas-
        //index.build(input, outpath, parser);
        
        //index.load(outpath);
        
        /*DEBUG
        for(Posting p : index.getTermPostings("c")){
            for(Long pos : p.getTermPositions()){
                System.out.println(pos);
            }
            System.out.println("_________________________");
        }
        */
        
    }
}
