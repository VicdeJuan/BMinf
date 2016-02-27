/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

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

        
        //Documentos que vamos a analizar:
        HashMap diccionarioDocs = new HashMap(); //(nombre del documento, id del documento)
        int idDoc = 0; //id
        
        // create a buffer to improve copy performance later.
        String value, texto = "";
        byte[] buffer = new byte[2048];
        
        InputStream theFile = new FileInputStream("/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docs1k.zip");
        ZipInputStream stream = new ZipInputStream(theFile);
        String outpath1 = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docIndice1";
        String outpath2 = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docIndice2";
        
        Indexar index = new Indexar();
        index.build("/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docs1k.zip", outpath1, null);
        
    }
}
