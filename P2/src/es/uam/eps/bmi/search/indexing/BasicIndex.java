package es.uam.eps.bmi.search.indexing;

import es.uam.eps.bmi.search.TextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.searching.BasicReader;
import es.uam.eps.bmi.search.searching.ModuloNombre;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class BasicIndex implements Index {

    private String indexPath;

    private HashMap<String, ModuloNombre> diccionarioDocs_NM;          //(docId -> (nombre del documento, modulo))
    private HashMap<String, Long> diccionarioTerminos_indice;  //(termino -> offset de bytes en el fichero de indice)

    private BasicReader reader;

    /**
     * Diccionario que asocia cada documento con su ModuloNombre.
     * @return el hashmap con el diccionario descrito.
     */
    public HashMap<String, ModuloNombre> getDiccionarioDocs_NM() {
        return diccionarioDocs_NM;
    }

     /**
     * Diccionario que asocia cada termino con su posicion en el indice.
     * @return Hashmap que contiene el diccionario descrito.
     */
    public HashMap<String, Long> getDiccionarioTerminos_indice() {
        return diccionarioTerminos_indice;
    }

    public void loadReader() throws FileNotFoundException, IOException, ClassNotFoundException {
        //this.reader = new BasicReader(this.indexPath);
        this.reader= new BasicReader(this.indexPath,this.diccionarioDocs_NM,this.diccionarioTerminos_indice);
    }

    public BasicReader getReader() {
       
        return reader;
    }

    /**
     * Constructor basico.
     */
    public BasicIndex() {
        this.indexPath = "";
        this.diccionarioTerminos_indice = new HashMap();
        this.diccionarioDocs_NM = new HashMap();
    }

    /**
     * Construye un índice a partir de una colección de documentos de texto.
     *
     * @param inputCollectionPath ruta de la carpeta en la que se encuentran los
     * documentos a indexar
     * @param outputIndexPath ruta de la carpeta en la que almacenar el índice
     * creado,
     * @param textParser parser de texto que procesará el texto de los
     * documentos para su indexación
     */
    @Override
    public void build(String inputCollectionPath, String outputIndexPath, TextParser textParser) {
        int idDoc = 1; //contador id de documentos
        this.indexPath = outputIndexPath;

        // buffer para guardar lectura del fichero (no tiene ninguna importancia)
        byte[] buffer = new byte[2048];

        //Acceso al zip con los documentos
        InputStream theFile;

        try {
            theFile = new FileInputStream(inputCollectionPath);

            ZipInputStream stream = new ZipInputStream(theFile);

            //Fichero de salida auxiliar
            String outpath2 = outputIndexPath + "_aux";

            //Controlar creación del indice por primera vez:
            Boolean flagPrimeraVez = true;
            Boolean flagIndice = false;
            Boolean quedanArchivos = true;
            Boolean docsVacio = true;

            //Numero total de bytes leidos;
            long numBytes = 0;

            //Creacion indice en RAM.
            Indice indice = new Indice(new ArrayList<Entrada>());;

            //Leemos uno por uno los documentos del zip mientras queden archivos en el zip
            ZipEntry entry;
            while (quedanArchivos == true) {

                //Cogemos siguiente documento del zip
                entry = stream.getNextEntry();

                //Si el indice de RAM no esta creado, lo creamos:
                if (flagIndice) {
                    flagIndice = false;
                    indice = new Indice(new ArrayList<Entrada>());
                }

                //Si hay ficheros en el zip
                if (entry != null) {
                    docsVacio = false; //Indicamos que el zip NO estaba vacío de documentos
                    long len = 0; //longitud en bytes de texto leído del documento actual
                    long pos_termino = 0; //posición del término en el documento
                    String value, texto = "";

                    System.out.println("Documento " + idDoc + "----" + entry.getName() + "----tamaño:" + entry.getSize());
                    System.out.println("Memoria total: " + Runtime.getRuntime().totalMemory() + "-- Memoria libre: " + Runtime.getRuntime().freeMemory() + "-- Memoria máxima: " + Runtime.getRuntime().maxMemory());
                    //System.out.println("Diferencia: " + numBytes);
                    //Obtenemos el texto en bruto del fichero:
                    while ((len = stream.read(buffer)) > 0) {
                        value = new String(buffer, 0, (int) len, "UTF-8");
                        texto = texto.concat(value);
                        numBytes = numBytes + len;
                    }

                    //parseamos el texto
                    texto = textParser.parse(texto);

                    //Añadimos documento en hashmap de documentos:
                    String nombreDocumento = entry.getName();
                    int docId_actual = idDoc;

                    // Para guardar tambien el modulo, 
                    //      este es el HashMap que utilizamos
                    diccionarioDocs_NM.putIfAbsent("" + docId_actual, new ModuloNombre(nombreDocumento, 0));

                    //Tokenizamos el texto, y cogemos uno a uno los términos metiéndolos en el indice
                    StringTokenizer tokens = new StringTokenizer(texto, " ,;:\n\r\t"); //PREGUNTAR SI ESTAN BIEN ESTOS SEPARADORES
                    while (tokens.hasMoreTokens()) {
                        String termino = tokens.nextToken();

                        //Parseamos el termino, decidimos si merece la pena meterlo en el diccionario:
                        // FALTA CODIGO
                        //Si el termino no esta en el indice lo metemos
                        if (!indice.contieneTermino(termino)) {
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
                        } else {
                            //Buscamos en la Entrada un posting con el mismo docId
                            Entrada entrada = indice.getEntrada(termino);

                            //Buscamos el posting que coincida con el id del documento en el que estamos
                            Posting posting = entrada.getPosting(String.valueOf(docId_actual));

                            //Si no lo encontramos, creamos un nuevo Posting con el documento:
                            if (posting == null) {
                                posting = new Posting();
                                posting.setDocId(String.valueOf(docId_actual));
                                posting.addTermPosition(pos_termino);

                                //Introducimos el posting en la entrada
                                entrada.addPosting(posting);

                                //Si lo encontramos, lo editamos
                            } else {
                                posting.addTermPosition(pos_termino);
                            }
                        }

                        //Se incrementa en uno la posición en el documento, y vamos con el siguiente token.
                        pos_termino++;
                    }//Fin del bucle de leer tokens 1 por 1 del documento
                    //Se incrementa en 1 el id del documento
                    idDoc++;
                }//fin del if de cargar indice en ram de un fichero del zip 

                //Si entry es null avisamos para que no se vuelva a leer de stream en la siguiente iteración
                if (entry == null) {
                    //Si además docsVacio es true, quiere decir que el zip estaba vacío de documentos
                    if (docsVacio == true) {
                        System.out.println("No hay documentos");
                        return;
                    }
                    //Indicamos que no quedan más archivos en el zip
                    quedanArchivos = false;
                }

                //A continuación se fusiona el índice que hemos creado en RAM, con el que tenemos en disco.
                //Miramos primero si vamos a fusionar este documento o esperamos a juntar mas.
                //Si entry es null, no podemos juntar más documentos y por tanto, fusionamos con lo que haya.
                if ((numBytes > Utils.RAM_LIMIT) || (entry == null)) {
                    System.out.println("FUSIONAMOS");
                    flagIndice = true; //Indicamos que para el próximo documento que leamos, hay que crear un nuevo indice en RAM
                    numBytes = 0; //Reinicializamos numero total de bytes leidos a 0
                    //long offset = 0; //Mide la posición en bytes del término en el índice 

                    //Si es la primera vez que vamos a guardar el indice en disco, tenemos que crear el documento:
                    if (flagPrimeraVez) {
                        System.out.println("PRIMERA VEZ");
                        flagPrimeraVez = false; //Indicamos que ya no es la primera que guardamos el índice en disco
                        FileWriter indice_disco = new FileWriter(outputIndexPath);
                        BufferedWriter bw = new BufferedWriter(indice_disco);

                        /*  ASÍ VAMOS A CREAR EL INDICE:
                                
                         Para escribir el índice, utilizar:

                         linea: termino ESPACIO lista_de_postings

                         lista_de_postings: posting
                         | posting ESPACIO lista_de_postings

                         posting: docId COMA NumPos COMA posiciones

                         posiciones: long
                         | long COMA posiciones

                         Por ejemplo:
                         Parra 142.45 1,3,1,2,3 2,2,1,2
                         termino modulo lista_de_postings_del_doc_1 lista_de_postings_del_doc_2
                         */
                        //El siguiente bucle escribe el índice en un documento txt:
                        for (Entrada e : indice.getListaEntradas()) {
                            String linea = e.getTermino();
                            for (Posting p : e.getListaPostings()) {
                                linea = linea + Utils.ExternPostingSeparator + String.valueOf(p.getDocId()) + Utils.InternPostingSeparator + String.valueOf(p.getNumTerms());
                                for (Long l : p.getTermPositions()) {
                                    linea = linea + Utils.InternPostingSeparator + String.valueOf(l);
                                }
                            }
                            linea = linea + "\n";
                            bw.write(linea);
                            bw.flush();

                            //Guardamos el par (termino, offset de bytes) en un diccionario
                            //offset = offset + linea.getBytes().length;
                            //this.diccionarioTerminos_indice.put(e.getTermino(), offset);
                        }
                        if (indice_disco != null) {
                            indice_disco.close();
                        }

                        // Si no, lo fusionamos con el ya existente
                    } else {
                        FileReader indice_disco = new FileReader(outputIndexPath);
                        BufferedReader br = new BufferedReader(indice_disco);
                        FileWriter indice_disco_2 = new FileWriter(outpath2);
                        BufferedWriter bw = new BufferedWriter(indice_disco_2);

                        String linea_leida;

                        //Leemos línea por línea el documento con el índice
                        while ((linea_leida = br.readLine()) != null) {
                            //Tokenizamos la línea para obtener el término y todos los valores:
                            StringTokenizer tokens_linea = new StringTokenizer(linea_leida, " \n\r");

                            //Nos guardamos el primer elemento, que es el término:
                            String termino_linea = tokens_linea.nextToken();

                            //Buscamos el termino en el indice que esta en RAM
                            //Si lo contiene, tenemos que modificar esa entrada del índice:
                            if (indice.contieneTermino(termino_linea)) {
                                //Guardamos la Entrada asociada al termino de RAM:
                                Entrada entrada_ram = indice.getEntrada(termino_linea);

                                //Guardamos la entrada asociada al termino, del disco
                                Entrada entrada_disco = new Entrada(termino_linea);
                                while (tokens_linea.hasMoreTokens()) {
                                    entrada_disco.addPosting(stringToPosting(tokens_linea.nextToken()));
                                }

                                        //DEBUG
                                        /*
                                 System.out.println("Termino repetido RAM: "+entrada_ram.getTermino()+" --- Disco: "+entrada_disco.getTermino());
                                 String linea_aux = "DISCO: "+entrada_disco.getTermino()+Utils.ExternPostingSeparator+String.valueOf(entrada_disco.getModulo());
                                 for(Posting p: entrada_disco.getListaPostings()){
                                 linea_aux = linea_aux+Utils.ExternPostingSeparator+String.valueOf(p.getDocId())+Utils.InternPostingSeparator+String.valueOf(p.getNumTerms());
                                 for(Long l : p.getTermPositions()){
                                 linea_aux = linea_aux+Utils.InternPostingSeparator+String.valueOf(l);
                                 }
                                 }
                                 linea_aux = linea_aux;
                                 System.out.println(linea_aux);
                                 linea_aux = "RAM: "+entrada_ram.getTermino()+Utils.ExternPostingSeparator+String.valueOf(entrada_ram.getModulo());
                                 for(Posting p: entrada_ram.getListaPostings()){
                                 linea_aux = linea_aux+Utils.ExternPostingSeparator+String.valueOf(p.getDocId())+Utils.InternPostingSeparator+String.valueOf(p.getNumTerms());
                                 for(Long l : p.getTermPositions()){
                                 linea_aux = linea_aux+Utils.InternPostingSeparator+String.valueOf(l);
                                 }
                                 }
                                 linea_aux = linea_aux;
                                 System.out.println(linea_aux);
                                 */
                                //Mezclamos ambas entradas:
                                Entrada entrada_mezclada = Entrada.mezclarEntradas(entrada_disco, entrada_ram);

                                /*
                                 linea_aux = "MEZCLA: "+entrada_mezclada.getTermino()+Utils.ExternPostingSeparator+String.valueOf(entrada_mezclada.getModulo());
                                 for(Posting p: entrada_mezclada.getListaPostings()){
                                 linea_aux = linea_aux+Utils.ExternPostingSeparator+String.valueOf(p.getDocId())+Utils.InternPostingSeparator+String.valueOf(p.getNumTerms());
                                 for(Long l : p.getTermPositions()){
                                 linea_aux = linea_aux+Utils.InternPostingSeparator+String.valueOf(l);
                                 }
                                 }
                                 linea_aux = linea_aux+"\n";
                                 System.out.println(linea_aux);
                                 */
                                //Reescribimos la nueva linea:
                                String linea_escribir = entrada_mezclada.getTermino();
                                for (Posting p : entrada_mezclada.getListaPostings()) {
                                    linea_escribir = linea_escribir + Utils.ExternPostingSeparator + String.valueOf(p.getDocId()) + Utils.InternPostingSeparator + String.valueOf(p.getNumTerms());
                                    for (Long l : p.getTermPositions()) {
                                        linea_escribir = linea_escribir + Utils.InternPostingSeparator + String.valueOf(l);
                                    }
                                }
                                linea_escribir = linea_escribir + "\n";
                                bw.write(linea_escribir);
                                bw.flush();

                                //Guardamos el par (termino, offset de bytes) en un diccionario
                                //offset = offset + linea_escribir.getBytes().length;
                                //this.diccionarioTerminos_indice.replace(termino_linea, offset);
                                //Eliminamos del indice de RAM la entrada correspondiente a ese término.:
                                indice.eliminarEntrada(entrada_ram);

                                // Si no lo contiene la volvemos a escribir tal cual:
                            } else {
                                linea_leida = linea_leida + "\n";
                                bw.write(linea_leida);
                                bw.flush();

                                //Guardamos el par (termino, offset de bytes) en un diccionario
                                //offset = offset + linea_leida.getBytes().length;
                                //this.diccionarioTerminos_indice.replace(termino_linea, linea_leida.getBytes().length);
                            }

                        }

                        //Ahora escribimos una línea nueva por cada término de RAM que quede sin introducir
                        for (Entrada e : indice.getListaEntradas()) {
                            if (indice.contieneTermino(e.getTermino())) {
                                String linea_escribir = e.getTermino();
                                for (Posting p : e.getListaPostings()) {
                                    linea_escribir = linea_escribir + Utils.ExternPostingSeparator + String.valueOf(p.getDocId()) + Utils.InternPostingSeparator + String.valueOf(p.getNumTerms());
                                    for (Long l : p.getTermPositions()) {
                                        linea_escribir = linea_escribir + Utils.InternPostingSeparator + String.valueOf(l);
                                    }
                                }
                                linea_escribir = linea_escribir + "\n";
                                bw.write(linea_escribir);
                                bw.flush();

                                //Guardamos el par (termino, offset de bytes) en un diccionario
                                //offset = offset + linea_escribir.getBytes().length;
                                //this.diccionarioTerminos_indice.replace(e.getTermino(), linea_escribir.getBytes().length);
                            }
                        }

                        if (indice_disco != null) {
                            indice_disco.close();
                        }
                        if (indice_disco_2 != null) {
                            indice_disco_2.close();
                        }

                        File f1 = new File(outputIndexPath);
                        File f2 = new File(outpath2);
                        f2.renameTo(f1);
                    }

                }//fin del if de fusionado

            }//Fin del while de coger archivos del zip

            stream.close();

            buildDics();
            writeDics();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Construye los diccionarios a partir del indice.
     */
    private void buildDics() {

      
        try {

            RandomAccessFile b = new RandomAccessFile(indexPath, "r");

            String line;
            String termino;
            while ((line = b.readLine()) != null) {
                int idx = line.indexOf(Utils.ESPACIO);
                termino = line.substring(0, idx);
                long p = b.getFilePointer();
                int l = line.getBytes().length + 1;
                diccionarioTerminos_indice.put(termino, p - l);

               //  **** El otro diccionario
                // La lista de postings
                ArrayList<String> post_list = new ArrayList<>(
                        Arrays.asList(line.split(Utils.ExternPostingSeparator)));
                // Eliminamos el termino
                post_list.remove(0);
                ArrayList<Posting> p_list = new ArrayList<>();

               // Iteramos sobre la lista [["termino"],["docid,pos1,pos2"],["docid2,pos1,pos2"]]
                //   convirtiendo cada termino de la lista en una lista de terminos, separando por
                //   comas
                post_list.stream().map((s) -> new ArrayList<String>(Arrays.asList(s.split(Utils.InternPostingSeparator)))).
		
                        forEach((posting) -> {
                            String docId = posting.get(0);
                            posting.remove(0);
                            // Obtenemos la lista de posiciones a partir del texto que forma el posting.
                            List<Long> positions = posting.stream().map(Long::parseLong).collect(Collectors.toList());
                            p_list.add(new Posting(docId, positions));
                            diccionarioDocs_NM.get(docId).updateModulo(Math.pow(Utils.tf_idf(termino,docId, p_list, this.getNumDocs()),2));
                        }
                        );
                diccionarioDocs_NM.forEach((k, v) -> v.setModulo(Math.sqrt(v.getModulo())));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicIndex.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Vuelva los diccionarios en fichero para su posterior uso.
     */
    private void writeDics() {
        try {
            ObjectOutputStream OOS;

            OOS = new ObjectOutputStream(new FileOutputStream(Utils.dicDocId_ModuloNombre_FILE));
            OOS.writeObject(diccionarioDocs_NM);
            OOS.close();

            OOS = new ObjectOutputStream(new FileOutputStream(Utils.dicTerminoOffset_FILE));
            OOS.writeObject(diccionarioTerminos_indice);
            OOS.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Convierte una cadena de texto del indice en un posting.
     * @param str Cadena de la forma: 
     *  "%s,%d,%d,%d,...",dicId,numPosiciones,posicion1,posicion2,...
     * @return El posting creado a partir de la cadena.
     */
    public static Posting stringToPosting(String str) {
        String[] s = str.split(",");

        Posting p;

        List<Long> termPositions = new ArrayList<>();
        String docId = s[0];
        String numTerms = s[1];
        for (int i = 0; i < Integer.parseInt(numTerms); i++) {
            //System.out.println("dentro de stringtoposting: "+s[i+2]);
            termPositions.add(Long.parseLong(s[i + 2]));
        }

        p = new Posting(docId, termPositions);
        return p;
    }

    @Override

    public void load(String indexPath) {

        this.indexPath = indexPath;

        try {
            //Cargamos en RAM el diccionario (docId, nombre del documento)
            ObjectInputStream ois
                    = new ObjectInputStream(new FileInputStream(Utils.dicDocId_ModuloNombre_FILE));
            this.diccionarioDocs_NM = (HashMap<String, ModuloNombre>) ois.readObject();
            ois.close();

            ois = new ObjectInputStream(new FileInputStream(Utils.dicTerminoOffset_FILE));
            this.diccionarioTerminos_indice = (HashMap<String, Long>) ois.readObject();
            ois.close();
            
            loadReader();

        } catch (IOException | ClassNotFoundException e) {
		System.out.println("Error: no se ha podido cargar el índice porque no ha sido creado con anterioridad");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
	    System.exit(-1);
        }
    }

  /*  public void load(String indexPath) {
         this.indexPath = indexPath;
         
         try{
            
            //Cargamos en RAM el diccionario (termino, offset en indice)
           RandomAccessFile br = new RandomAccessFile(indexPath, "r");
           String linea_leida;
           while ((linea_leida = br.readLine()) != null) {
                //Tokenizamos la línea para obtener el término y todos los valores:
                StringTokenizer tokens_linea = new StringTokenizer(linea_leida, " \n\r");
                String termino = tokens_linea.nextToken();
                this.diccionarioTerminos_indice.put(termino, br.getFilePointer()-linea_leida.getBytes().length-1);
            }
            br.close();}
         catch(Exception e){}
    }
    */
    @Override
    public String getPath() {
        return this.indexPath;
    }

    @Override
    public List<String> getDocIds() {
        return new ArrayList<>(this.diccionarioDocs_NM.keySet());
    }

    @Override
    public TextDocument getDocument(String docId) {
        TextDocument td = new TextDocument(docId, this.diccionarioDocs_NM.get(docId).getNombre());
        return td;
    }

    @Override
    public List<String> getTerms() {
        return new ArrayList<>(this.diccionarioTerminos_indice.keySet());
    }

    @Override
    public List<Posting> getTermPostings(String term) {
        ArrayList<Posting> lp = new ArrayList<>();

        Long offset = this.diccionarioTerminos_indice.get(term);

        try {
            RandomAccessFile br = new RandomAccessFile(this.indexPath, "r");
            br.seek(offset);
            String linea = br.readLine();

            //Tokenizamos la línea para obtener el término y todos los valores:
            StringTokenizer tokens_linea = new StringTokenizer(linea, " \n\r");

            //Nos guardamos el primer elemento, que es el término:
            String termino_linea = tokens_linea.nextToken();

            System.out.println(term + "--" + termino_linea + "--" + linea);

            if (term.equals(termino_linea)) {
                while (tokens_linea.hasMoreTokens()) {
                    lp.add(stringToPosting(tokens_linea.nextToken()));
                }
            } else {
                throw new Exception("El término no coindice con el offset en el indice.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return lp;
    }

    /**
    * 
    * @return el numero de documentos que tiene el indice.
    */
    public double getNumDocs() {
        return this.diccionarioTerminos_indice.size();
    }

}
