/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 *
 * @author e267044
 */
public class BasicReader {
    File indice;
    RandomAccessFile accesoIndice;
    HashMap<String,Long> dicoffset;//diccionario de offset para cada termino
    
    //accedo al indice, con modo lectura
    public BasicReader(File indice,String TermOffset) throws FileNotFoundException, IOException {
        this.indice = indice;
        this.accesoIndice = new RandomAccessFile(indice,"r");
        String cadena;
        
        FileReader f = new FileReader(TermOffset);
        try (BufferedReader b = new BufferedReader(f)) {
            while((cadena = b.readLine())!=null) {
                
                String[] aux= cadena.split(" ");
                Long offset;
                offset=Long.parseLong(aux[1]);
                dicoffset.put(aux[0], offset);
                System.out.println(cadena);
            } 
        }
    }
    
    
    public String leerlinea(String termino) throws FileNotFoundException, IOException{
        String linea;
        Long offset= dicoffset.get(termino);
        
        accesoIndice.seek(offset);
        linea = accesoIndice.readLine();
        /*        
        String[] cadena=linea.split(" ");
        //0 termino 1 modulo 2 lista de postings k estan separados por comas
    */
    return linea;
    }
    
    
}
