/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.TextParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author parra
 */
public class Indexar {
   private HashMap diccionarioDocs; //(docId, nombre del documento)
   private HashMap diccionarioTerminos_indice; //(termino, offset de bytes en el fichero de indice)
    
        
    
        public Indexar(){
        this.diccionarioDocs = new HashMap();
        this.diccionarioTerminos_indice = new HashMap();
    }
    
        
    /**
	 * Construye un índice a partir de una colección de documentos de texto.
	 *
	 * @param inputCollectionPath ruta de la carpeta en la que se encuentran
	 * los documentos a indexar
	 * @param outputIndexPath ruta de la carpeta en la que almacenar el
	 * índice creado,
	 * @param textParser parser de texto que procesará el texto de los
	 * documentos para su indexación
	 */
	public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) throws FileNotFoundException, IOException{
            //Documentos que vamos a analizar:
            HashMap diccionarioDocs = new HashMap(); //(nombre del documento, id del documento)
            int idDoc = 0; //id

            // create a buffer to improve copy performance later.
            byte[] buffer = new byte[2048];

            //Acceso al zip con los documentos
            InputStream theFile = new FileInputStream(inputCollectionPath);
            ZipInputStream stream = new ZipInputStream(theFile);
            
            //Fichero de salida auxiliar
            String outpath2 = outputIndexPath+"_aux";
            
            //Controlar creación del indice por primera vez:
            Boolean flagPrimeraVez = true;
            Boolean flagIndice = false;
            Boolean quedanArchivos = true;
            Boolean docsVacio = true;

            //Numero de bytes leidos;
            long numBytes = 0;

            //Indice en RAM.
            Indice indice = new Indice(new ArrayList<Entrada>());;
            
            try{
                //Leemos uno por uno los documentos del zip
                ZipEntry entry;
                while(quedanArchivos == true)
                {
                //while(((entry = stream.getNextEntry())!=null)) // && (idDoc <= 1)
                //{
                    entry = stream.getNextEntry();
                    
                    //DEBUG: Cosas que se pueden obtener de entry
                    //DEBUG: String s = String.format("Entry: %s len %d added %TD",entry.getName(), entry.getSize(),new Date(entry.getTime()));

                    if(flagIndice){
                        flagIndice = false;
                        
                        //Eliminamos indice anterior de RAM ¿necesario?
                        //FALTA CODIGO

                        //Creamos un índice en RAM:
                        indice = new Indice(new ArrayList<Entrada>());
                    }

                    try
                    {

                        if(entry!=null){
                            docsVacio = false;
                            long len = 0; //longitud en bytes de texto leído
                            long pos_termino = 0; //posición del término en el documento
                            String value, texto = "";
                            
                            System.out.println("Documento "+idDoc+"----"+entry.getName()+"----tamaño:"+entry.getSize());
                                
                            //Obtenemos el texto en bruto del fichero:
                            while ((len = stream.read(buffer)) > 0){
                                value = new String(buffer, 0, (int) len, "UTF-8");
                                //value = new String(buffer, "UTF-8").substring(0, len-1);
                                texto = texto.concat(value);        
                                //output.write(buffer, 0, len)
                                numBytes = numBytes + len;                   
                            }

                            //Añadimos documento en hashmap de documentos:
                            String nombreDocumento = entry.getName();
                            int docId_actual = idDoc;
                            if (!diccionarioDocs.containsValue(nombreDocumento)){
                                diccionarioDocs.put(docId_actual,nombreDocumento);
                            }

                            //Creamos el índice para este documento:
                            //Indice indice = new Indice(new ArrayList<Entrada>());

                            //Tokenizamos el texto, y cogemos uno a uno los términos metiéndolos en el indice
                            StringTokenizer tokens=new StringTokenizer(texto, " ,;:\n\r\t"); //PREGUNTAR SI ESTAN BIEN ESTOS SEPARADORES
                            System.out.println("Longiud token: "+tokens.countTokens());
                            while(tokens.hasMoreTokens()){
                                String termino = tokens.nextToken();

                                //Parseamos el termino, decidimos si merece la pena meterlo en el diccionario:
                                // FALTA CODIGO

                                //Si el termino no esta en el indice lo metemos
                                if (!indice.contieneTermino(termino)){                            
                                    //Creamos un posting nuevo:
                                    Posting posting = new Posting();
                                    posting.setDocId(String.valueOf(docId_actual));
                                    posting.addTermPosition(pos_termino);

                                    //Creamos una entrada nueva para el indice:
                                    Entrada entrada = new Entrada(termino);
                                    //Introducimos el posting en la entrada
                                    entrada.addPosting(posting);

                                    //Añadimos la entrada al indice:
                                    indice.addEntrada(entrada);

                                //Si ya está, tenemos que editar su información:    
                                }else{
                                    //Buscamos en la Entrada un posting con el mismo docId
                                    Entrada entrada = indice.getEntrada(termino);

                                    //Buscamos el posting que coincida con el id del documento en el que estamos
                                    Posting posting = entrada.getPosting(String.valueOf(docId_actual));


                                    //Si no lo encontramos, creamos un nuevo Posting con el documento:
                                    if(posting == null){
                                        posting = new Posting();
                                        posting.setDocId(String.valueOf(docId_actual));
                                        posting.addTermPosition(pos_termino);

                                        //Introducimos el posting en la entrada
                                        entrada.addPosting(posting);

                                    //Si lo encontramos, lo editamos
                                    }else{
                                        posting.addTermPosition(pos_termino);
                                    }               

                                }

                                //Se incrementa en uno la posición en el documento, y vamos con el siguiente token.
                                pos_termino++;
                            }
                            //System.out.println("Documento "+idDoc+"----"+entry.getName()+"----bytesLeidos:"+numBytes);
                            idDoc++;
                        }//fin del if de cargar indice en ram de un fichero 

                        //Si entry es null avisamos para que no se vuelva a leer de stream
                        if(entry==null){
                            if(docsVacio == true){
                                System.out.println("No hay documentos");
                                return;
                            }
                            quedanArchivos = false;
                        }

                        //Ahora tenemos que fusionar el índice que hemos creado en RAM, con el que tenemos en disco:

                        //Miramos primero si vamos a fusionar este documento o esperamos a juntar mas:
                        if((numBytes > 500000000)||(entry==null)){
                            System.out.println("ENTRAMOS AQUI");
                            flagIndice = true;
                            numBytes = 0;

                            //Si es el primero documento el que leemos, lo creamos
                            long offset = 0;
                            if(flagPrimeraVez){
                                System.out.println("PRIMERA VEZ");
                                flagPrimeraVez = false;
                                FileWriter indice_disco = new FileWriter(outputIndexPath);
                                BufferedWriter bw = new BufferedWriter(indice_disco);

                                /*
                                    Para escribir el índice, utilizar:

                                    linea: termino ESPACIO modulo_del_documento ESPACIO lista_de_postings

                                    lista_de_postings: posting
                                                     | posting ESPACIO lista_de_postings

                                    posting: docId COMA NumPos COMA posiciones

                                    posiciones: long
                                              | long COMA posiciones

                                    Por ejemplo:
                                    Parra 142.45 1,3,1,2,3 2,2,1,2
                                    termino modulo lista_de_postings_del_doc_1 lista_de_postings_del_doc_2
                                */
                                for (Entrada e : indice.getListaEntradas()){
                                    String linea = e.getTermino()+Utils.ESPACIO+String.valueOf(e.getModulo());
                                    for(Posting p : e.getListaPostings()){    
                                        linea = linea+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                        for(Long l : p.getTermPositions()){
                                            linea = linea+Utils.COMA+String.valueOf(l);
                                        }
                                    }
                                    linea = linea+"\n";
                                    bw.write(linea);
                                    bw.flush();

                                    //Guardamos el par (termino, offset de bytes) en un diccionario
                                    offset = offset + linea.getBytes().length;
                                    this.diccionarioTerminos_indice.put(e.getTermino(), offset);
                                }
                                if(indice_disco!=null){
                                    indice_disco.close();
                                }
                            // Si no, lo fusionamos con el ya existente
                            }else{
                                FileReader indice_disco = new FileReader(outputIndexPath);
                                BufferedReader br = new BufferedReader(indice_disco);
                                FileWriter indice_disco_2 = new FileWriter(outpath2);
                                BufferedWriter bw = new BufferedWriter(indice_disco_2);

                                String linea_leida;

                                //Leemos línea por línea el documento con el índice
                                while((linea_leida=br.readLine())!=null){
                                    //Tokenizamos la línea para obtener el término y todos los valores:
                                    StringTokenizer tokens_linea=new StringTokenizer(linea_leida, " \n\r");

                                    //Nos guardamos el primer elemento, que es el término:
                                    String termino_linea = tokens_linea.nextToken();
                                    
                                    //No guardamos el segundo elemento, que es el módulo:
                                    String modulo_linea = tokens_linea.nextToken();
                                    
                                    //Buscamos el termino en el HashMap del índice que esta en RAM
                                    //Si lo contiene, tenemos que modificar esa entrada del índice:
                                    if(indice.contieneTermino(termino_linea)){
                                        //Para fusionar, metemos la Entrada de esa línea a Ram:
                                        //Guardamos la Entrada asociada al termino de RAM:
                                        Entrada entrada_ram = indice.getEntrada(termino_linea);
                                        
                                        //Guardamos la entrada asociada al termino, del disco
                                        Entrada entrada_disco = new Entrada(termino_linea, Long.parseLong(modulo_linea));
                                        while(tokens_linea.hasMoreTokens()){
                                            //String termino_posicion = tokens_linea.nextToken();
                                            //System.out.println(termino_posicion);
                                            //Posting p = stringToPosting(tokens_linea.nextToken());
                                            //System.out.println("docid: "+p.getDocId());
                                            //System.out.println("posiciones: "+String.valueOf(p.getNumTerms()));
                                            /*for(Long l: p.getTermPositions()){
                                                System.out.println(l);
                                            }*/
                                            entrada_disco.addPosting(stringToPosting(tokens_linea.nextToken()));
                                            
                                        }

                                        
                                        //DEBUG
                                        /*
                                        System.out.println("Termino repetido RAM: "+entrada_ram.getTermino()+" --- Disco: "+entrada_disco.getTermino());
                                        String linea_aux = "DISCO: "+entrada_disco.getTermino()+Utils.ESPACIO+String.valueOf(entrada_disco.getModulo());
                                        for(Posting p: entrada_disco.getListaPostings()){
                                            linea_aux = linea_aux+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                            for(Long l : p.getTermPositions()){
                                                linea_aux = linea_aux+Utils.COMA+String.valueOf(l);
                                            }
                                        }
                                        linea_aux = linea_aux;
                                        System.out.println(linea_aux);
                                        linea_aux = "RAM: "+entrada_ram.getTermino()+Utils.ESPACIO+String.valueOf(entrada_ram.getModulo());
                                        for(Posting p: entrada_ram.getListaPostings()){
                                            linea_aux = linea_aux+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                            for(Long l : p.getTermPositions()){
                                                linea_aux = linea_aux+Utils.COMA+String.valueOf(l);
                                            }
                                        }
                                        linea_aux = linea_aux;
                                        System.out.println(linea_aux);
                                        */
                                        
                                        //Mezclamos ambas entradas:
                                        Entrada entrada_mezclada = Entrada.mezclarEntradas(entrada_disco, entrada_ram);
                                        
                                        /*
                                        linea_aux = "MEZCLA: "+entrada_mezclada.getTermino()+Utils.ESPACIO+String.valueOf(entrada_mezclada.getModulo());
                                        for(Posting p: entrada_mezclada.getListaPostings()){
                                            linea_aux = linea_aux+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                            for(Long l : p.getTermPositions()){
                                                linea_aux = linea_aux+Utils.COMA+String.valueOf(l);
                                            }
                                        }
                                        linea_aux = linea_aux+"\n";
                                        System.out.println(linea_aux);
                                        */
                                                
                                        //Reescribimos la nueva linea:
                                        String linea_escribir = entrada_mezclada.getTermino()+Utils.ESPACIO+String.valueOf(entrada_mezclada.getModulo());
                                        for(Posting p : entrada_mezclada.getListaPostings()){    
                                            linea_escribir = linea_escribir+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                            for(Long l : p.getTermPositions()){
                                                linea_escribir = linea_escribir+Utils.COMA+String.valueOf(l);
                                            }
                                        }
                                        linea_escribir = linea_escribir+"\n";
                                        bw.write(linea_escribir);
                                        bw.flush();

                                        //Guardamos el par (termino, offset de bytes) en un diccionario
                                        offset = offset + linea_escribir.getBytes().length;
                                        this.diccionarioTerminos_indice.replace(termino_linea, offset);

                                        //Eliminamos del indice de RAM la entrada correspondiente a ese término.:
                                        indice.eliminarEntrada(entrada_ram);

                                    // Si no lo contiene la volvemos a escribir tal cual:
                                    }else{
                                        linea_leida = linea_leida+"\n";
                                        bw.write(linea_leida);
                                        bw.flush();

                                        //Guardamos el par (termino, offset de bytes) en un diccionario
                                        offset = offset + linea_leida.getBytes().length;
                                        this.diccionarioTerminos_indice.replace(termino_linea, linea_leida.getBytes().length);
                                    }

                                }

                                //Ahora escribimos una línea nueva por cada término de RAM que quede sin introducir
                                for (Entrada e : indice.getListaEntradas()){
                                    if(indice.contieneTermino(e.getTermino())){
                                        String linea_escribir = e.getTermino()+Utils.ESPACIO+String.valueOf(e.getModulo());
                                        for(Posting p : e.getListaPostings()){    
                                            linea_escribir = linea_escribir+Utils.ESPACIO+String.valueOf(p.getDocId())+Utils.COMA+String.valueOf(p.getNumTerms());
                                            for(Long l : p.getTermPositions()){
                                                linea_escribir = linea_escribir+Utils.COMA+String.valueOf(l);
                                            }
                                        }
                                        linea_escribir = linea_escribir+"\n";
                                        bw.write(linea_escribir);
                                        bw.flush();

                                        //Guardamos el par (termino, offset de bytes) en un diccionario
                                        offset = offset + linea_escribir.getBytes().length;
                                        this.diccionarioTerminos_indice.replace(e.getTermino(), linea_escribir.getBytes().length);
                                    }
                                }

                                if(indice_disco!=null) indice_disco.close();
                                if(indice_disco_2!=null) indice_disco_2.close();

                                File f1 = new File(outputIndexPath);
                                File f2 = new File(outpath2);
                                boolean correcto = f2.renameTo(f1);
                            }
                            
                        }//fin del if de fusionado
                    }finally{

                    }


                }
            
        }finally{
                // we must always close the zip file.
                stream.close();
            }
    }

    
        
        
    public static Posting stringToPosting(String str){
        String[] s = str.split(",");
        
        Posting p;
        
        List<Long> termPositions = new ArrayList<Long>();
        String docId = s[0];
        String numTerms = s[1];
        for (int i=0; i< Integer.parseInt(numTerms); i++){
            //System.out.println("dentro de stringtoposting: "+s[i+2]);
            termPositions.add(Long.parseLong(s[i+2]));
        }
        
        p = new Posting(docId, termPositions);
        return p;
    }
}

    
    

        