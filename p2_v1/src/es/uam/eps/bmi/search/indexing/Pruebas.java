/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

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
public class Pruebas {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        
        //Documentos que vamos a analizar:
        HashMap diccionarioDocs = new HashMap(); //(nombre del documento, id guardado en indice)
        int idDoc = 0; //id
        
        // create a buffer to improve copy performance later.
        String value, texto = "";
        byte[] buffer = new byte[2048];
        
        InputStream theFile = new FileInputStream("/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docs.zip");
        ZipInputStream stream = new ZipInputStream(theFile);
        String outpath1 = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docIndice1";
        String outpath2 = "/home/parra/NetBeansProjects/p2_v1/datos/clueweb-1K/docIndice2";
        
        try
        {

            // now iterate through each item in the stream. The get next
            // entry call will return a ZipEntry for each file in the
            // stream
            //FileOutputStream indice_disco = new FileOutputStream(outpath);
            ZipEntry entry;
            while(((entry = stream.getNextEntry())!=null) && (idDoc <= 0))
            {
                
                //String s = String.format("Entry: %s len %d added %TD",entry.getName(), entry.getSize(),new Date(entry.getTime()));

                // Once we get the entry from the stream, the stream is
                // positioned read to read the raw data, and we keep
                // reading until read returns 0 or less.
                //String outpath = outdir + "/" + entry.getName();
                //FileOutputStream output = null;
                
                
                
                try
                {
                    int len = 0; //longitud en bytes de texto leído
                    long posDoc = 0; //posición del término en el documento
                    
                    
                    //Obtenemos el texto en bruto del fichero:
                    while ((len = stream.read(buffer)) > 0){
                        value = new String(buffer, "UTF-8").substring(0, len);
                        texto = texto.concat(value);        
                        //output.write(buffer, 0, len);                        
                    }
                    
                    
                    
                    //Añadimos documento en hashmap de documentos:
                    String nombreDocumento = entry.getName();
                    int id_actual = idDoc;
                    if (!diccionarioDocs.containsValue(nombreDocumento)){
                        diccionarioDocs.put(id_actual,nombreDocumento);
                    }
             
                    
                    //Creamos el índice para este documento:
                    ArrayList<ArrayList<Posting>> indice = new ArrayList<ArrayList<Posting>>();
                    
                    //Creamos el HashMap de los terminos:
                    HashMap diccionarioTerminos = new HashMap();
                    
                    //Tokenizamos el texto, y cogemos uno a uno los términos metiendolos en el indice
                    StringTokenizer tokens=new StringTokenizer(texto, " ,;:\n"); //PREGUNTAR SI ESTAN BIEN ESTOS SEPARADORES
                    while(tokens.hasMoreTokens()){
                        String termino = tokens.nextToken();
                        
                        //Parseamos el termino, decidimos si merece la pena meterlo en el diccionario:
                        // FALTA CODIGO
                        
                        //Si el termino no esta en el indice lo metemos
                        if (!diccionarioTerminos.containsKey(termino)){
                            //Guardamos en un diccionario la posición de los postings del termino en el arrayList del indice
                            diccionarioTerminos.put(termino,indice.size());
             
                            //Creamos un posting nuevo:
                            List<Long> termPositions = new ArrayList<Long>();
                            termPositions.add(posDoc);
                            Posting posting = new Posting(String.valueOf(id_actual), termino, termPositions);
                            ArrayList<Posting> listaPostings = new ArrayList<Posting>();
                            listaPostings.add(posting);
                            
                            //Lo añadimos al indice
                            indice.add(listaPostings);
                        
                        //Si ya está, tenemos que editar su información:    
                        }else{
                            //Obtenemos la posicion en la que se encuentra la lista de postings de este termino en el indice 
                            int posArray = (int) diccionarioTerminos.get(termino);
                            
                            //Obtenemos la lista de postings asociadas al término:
                            ArrayList<Posting> listaPostings = indice.get(posArray);
                            
                            //Buscamos el posting que coincida con el id del documento en el que estamos
                            Boolean noEncontrado = true;
                            int i=0;
                            while(noEncontrado && i<listaPostings.size()){
                                if(listaPostings.get(i).getDocId().compareTo(String.valueOf(id_actual))==0){
                                    noEncontrado = false;
                                }else{
                                    i++;
                                }
                            }
                            
                            //Si no lo encontramos, creamos un nuevo Posting con el documento:
                            if(noEncontrado){
                                List<Long> termPositions = new ArrayList<Long>();
                                termPositions.add(posDoc);
                                Posting posting = new Posting(String.valueOf(id_actual), termino, termPositions);
                                listaPostings.add(posting);
                            
                            //Si lo encontramos, lo editamos
                            }else{
                                Posting posting = listaPostings.get(i);
                                posting.addTermPosition(posDoc);
                            }               
                            
                        }
                        
                        //Se incrementa en uno la posición en el documento, y vamos con el siguiente token.
                        posDoc++;
                    }
                    
                    //HashMap para los terminos del índice final (termino, linea en el fichero de indices)
                    HashMap diccionarioTerminos_indice = new HashMap();
                    
                    //Ahora tenemos que fusionar el índice que hemos creado en RAM, con el que tenemos en disco:
                    //Si es el primero documento el que leemos, lo creamos
                    if(idDoc == 0){
                        FileWriter indice_disco = new FileWriter(outpath1);
                        BufferedWriter pw = new BufferedWriter(indice_disco);
                        
                        for (int i=0; i<indice.size();i++){
                            //Guardamos el par (termino, linea del fichero de indices) en un diccionario
                            diccionarioTerminos_indice.put(indice.get(i).get(0).getTerm(), i+1);
                            
                            //Guardamos una linea con termino+doc1+numApariciones1+posApar1_1+...+posApar1_n+doc2+numApariciones2+posApar2_1+...+posApar2_n+...+docn+numAparicionesn+posAparn_1+...+posAparn_n
                            String linea = indice.get(i).get(0).getTerm();
                            for(int j=0; j<indice.get(i).size(); j++){
                                String idDoc_aux = indice.get(i).get(j).getDocId();
                                int numTerms = indice.get(i).get(j).getTermPositions().size();
                                linea = linea+" "+String.valueOf(idDoc_aux)+" "+numTerms;
                                for(int w=0; w<numTerms; w++){
                                    linea = linea+" "+String.valueOf(indice.get(i).get(j).getTermPositions().get(w));
                                }
                            }
                            pw.write(linea+"\n");
                            pw.flush();
                        }
                        if(indice_disco!=null){
                            indice_disco.close();
                        }
                    // Si no, lo fusionamos con el ya existente
                    }else{
                        FileReader indice_disco = new FileReader(outpath1);
                        BufferedReader br = new BufferedReader(indice_disco);
                        FileWriter indice_disco_2 = new FileWriter(outpath2);
                        BufferedWriter bw = new BufferedWriter(indice_disco_2);
                        
                        String linea;
                        int num_linea = 0;
                        
                        //Leemos línea por línea el documento con el índice
                        while((linea=br.readLine())!=null){
                            //Tokenizamos la línea para obtener el término y todos los valores:
                            StringTokenizer tokens_linea=new StringTokenizer(linea, " ");
                            
                            //Nos guardamos el primer elemento, que es el término:
                            String termino_linea = tokens_linea.nextToken();
                            
                            //Buscamos en el HashMap del índice con el que lo queremos fusionar que esta en RAM
                            //Si lo contiene, tenemos que modificar esa línea del índice:
                            if(diccionarioTerminos.containsKey(termino_linea)){
                                //Guardamos  en el diccionario el par (termino, linea del documento)
                                diccionarioTerminos_indice.put(termino_linea, num_linea);
                                
                                //Para fusionar, metemos la lista de postings de esa línea a Ram:
                                
                                
                                
                                //Cargamos la lista de postings asociada al termino:
                                int posArray_2 = (int) diccionarioTerminos.get(termino_linea);
                                ArrayList<Posting> postings_fusion = indice.get(posArray_2);
                                for(int i=0; i<postings_fusion.size(); i++){
                                    String idDoc_fusion = postings_fusion.get(i).getDocId();
                                    
                                    //Buscamos ese idDoc en la linea leída del documento de indice:
                                    
                                }
                            }else{
                                
                            }
                            
                            num_linea++;
                            
                        }
                        if(indice_disco!=null) indice_disco.close();
                        if(indice_disco_2!=null) indice_disco_2.close();
                        
                    }
                    
                    //value = new String(buffer, "UTF-8");
                    //texto=texto+value;
                    //lentotal = lentotal +len;
                    //System.out.println("______________________________");
                    //System.out.println(texto);
                    //System.out.println("______________________________");
                    //System.out.println(value+"-----");
                    //System.out.println(entry.getName());
                    //Incrementamos en 1 el id:
                    idDoc++;
                }
                finally
                {
                    // we must always close the output file
                    //if(indice_disco!=null) indice_disco.close();
                }
            }
        }
        finally
        {
            // we must always close the zip file.
            stream.close();
        }
    }
}

