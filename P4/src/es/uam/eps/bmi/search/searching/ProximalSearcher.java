/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.searching;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.indexing.Index;
import es.uam.eps.bmi.search.indexing.Posting;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.QueryParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.parsing.XMLReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author parra
 */
public class ProximalSearcher implements Searcher {

    BasicReader indexReader;
    private final static int TOP = 5;
    private final static int MOSTRAR = 10;

    /**
     * Crea el buscador a través del índice pasado por argumento.
     *
     * @param index indice creado
     */
    @Override
    public void build(Index index) {
        Index aux = index;
        aux.loadReader();
        this.indexReader = aux.getReader();
    }

    @Override
    public List<ScoredTextDocument> search(String query) {
        List<ScoredTextDocument> res = new ArrayList();

        //En un HashMap iremos guardando una lista con los postings asociados a cada docId comun
        HashMap<String, ArrayList<Posting>> hm = new HashMap<String, ArrayList<Posting>>();

        //Guardamos una lista de los docIds comunes
        ArrayList<String> ld = new ArrayList<String>();

        //Separamos la query en terminos.
        long query_length = 0;
        StringTokenizer tokens = new StringTokenizer(query, Utils.DefaultSeparators);

        while (tokens.hasMoreTokens()) {
            //Por cada término de la query...
            String termino = tokens.nextToken();
            termino = termino.toLowerCase();

            //Aumentamos en 1 la longitud de la query
            query_length++;

            //Guardamos la lista de postings que contiene
            ArrayList<Posting> lp_termino = this.indexReader.getTermPostings(termino);

            //System.out.println(termino+": "+lp_termino.size());
            if ((lp_termino == null) || (lp_termino.size() == 0)) {
                //System.out.println("El término '" + termino + "' no se encuentra en ningún documento");
                return null;
            }

            /*
             //DEBUG: Ver los postings con los que tratamos
             String texto2 = "";
             for (Posting p : lp_termino) {
             texto2 = texto2 + "(";
             for (Long l : p.getTermPositions()) {
             texto2 = texto2 + l + ",";
             }
             texto2 = texto2.substring(0, texto2.length() - 1) + ") ";
             }
             System.out.println(texto2);
             */
            //Para el primer término, todos los docId's son comunes
            if (hm.isEmpty()) {
                for (Posting p : lp_termino) {
                    ArrayList<Posting> lp_aux = new ArrayList<Posting>();
                    lp_aux.add(p);
                    hm.put(p.getDocId(), lp_aux);
                    ld.add(p.getDocId());
                }
            } //Para los siguientes términos, eliminamos los no comunes
            else {
                String docId;
                for (int posicion = 0; posicion < ld.size(); posicion++) {
                    //Por cada docId comun
                    docId = ld.get(posicion);

                    //Vamos a mirar si ese docId pertenece a la lista de postings del termino nuevo
                    boolean elemento_no_esta = true;
                    Posting p;
                    for (int i = posicion; i < lp_termino.size(); i++) {
                        p = lp_termino.get(i);
                        if (p.getDocId().equals(docId)) {
                            elemento_no_esta = false;
                            ArrayList<Posting> lp_aux = hm.get(docId);
                            lp_aux.add(p);
                            hm.replace(docId, lp_aux);
                        }
                    }

                    //Si no esta lo eliminamos de los comunes
                    if (elemento_no_esta) {
                        hm.remove(docId);
                        ld.remove(docId);
                        posicion--;
                    }
                }
            }

            /*
             //DEBUG: COGER BIEN LOS DOCUMENTOS IMPLICADOS EN LA CONSULTA
             System.out.println();
             for (String s : ld) {
             String texto = "";
             for (Posting p : hm.get(s)) {
             texto = texto + "(";
             for (Long l : p.getTermPositions()) {
             texto = texto + l + ",";
             }
             texto = texto.substring(0, texto.length() - 1) + ") ";
             }
             System.out.println(s + " " + this.getDocName(s) + ": " + texto);
             }
             */
        }

        /*
         //DEBUG: COGER BIEN LOS DOCUMENTOS IMPLICADOS EN LA CONSULTA
         System.out.println("\nFINALIZAMOS");
         for (String s : ld) {
         String texto = "";
         for (Posting p : hm.get(s)) {
         texto = texto + "(";
         for (Long l : p.getTermPositions()) {
         texto = texto + l + ",";
         }
         texto = texto.substring(0, texto.length() - 1) + ") ";
         }
         System.out.println(s + " " + this.getDocName(s) + ": " + texto);
         }
         */
        //Comienza la búsqueda proximal por cada docId:
        double score;
        for (String docId : ld) {
            //System.out.println("\n_____" + this.getDocName(docId) + "_____");
            ArrayList<Posting> lp = hm.get(docId);
            long a = -1;
            long b = -1;
            long aux, aux2;
            boolean no_hay_valores_mayores_que_a = true;
            score = 0;

            while (true) {
                //Obtenemos b:
                aux = -1;
                for (Posting p : lp) {
                    no_hay_valores_mayores_que_a = true;
                    for (Long l : p.getTermPositions()) {
                        if (l > a) {
                            if (aux < l) {
                                aux = l;
                            }
                            no_hay_valores_mayores_que_a = false;
                            break;
                        }
                    }

                    //Si no encontramos ningun valor mayor que a se acaba el algoritmo:
                    if (no_hay_valores_mayores_que_a) {
                        break;
                    }
                }

                //Si no encontramos ningun valor mayor que a se acabo el algoritmo:
                if (no_hay_valores_mayores_que_a) {
                    break;
                }

                b = aux;

                //Obtenemos a:
                aux = -1;
                aux2 = -1;
                for (Posting p : lp) {
                    for (Long l : p.getTermPositions()) {
                        if (l <= b) {
                            aux2 = l;
                        } else if (l > b) {
                            if ((aux == -1) || (aux2 < aux)) {
                                aux = aux2;
                            }
                            break;
                        }
                    }
                    if ((aux == -1) || (aux2 < aux)) {
                        aux = aux2;
                    }
                }
                a = aux;

                score = score + ((double) 1) / ((double) (b - a - query_length + 2));
                //System.out.println("(" + a + "," + b + ")");
            }
            //System.out.println("Score documento " + this.getDocName(docId) + ": " + score);
            res.add(new ScoredTextDocument(docId, score));
        }

        Collections.sort(res);
        return res;
    }

    @Override
    public String getDocName(String docId) {
        return this.indexReader.getDiccionarioDocs_NM().get(docId).getNombre();
    }

    public static void main(String[] args) throws IOException {

        // Directorio en el que se encuentra el índice en disco
        //String indexDir = "/home/parra/Escritorio/p3Bminf/indices/1K/basic/";
        //String indexDir = "/home/parra/Escritorio/p3Bminf/clueweb-1K-new/";
        //String indexDir = "/home/parra/Escritorio/p3Bminf/pruebas/";
        String indexDir = "colecciones/clueweb-1K/";

        // Cargamos el índice de disco
        BasicIndex basicIdx = new BasicIndex();
        basicIdx.load(indexDir);

        // Creamos la clase de búsqueda
        ProximalSearcher ps = new ProximalSearcher();

        //Cargamos el indice en la clase, en una variable BasicReader que permite leer el índice
        ps.build(basicIdx);
        BasicReader ir = ps.getIndexReader();

        //Leemos de teclado las querys
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n______ BÚSQUEDA PROXIMAL _______");
        System.out.println("Introducir las palabras de la búsqueda:");
        br = new BufferedReader(new InputStreamReader(System.in));
        String query = br.readLine();

        List<ScoredTextDocument> resul = ps.search(query);

        //Mostramos el resultado:
        //Obtenemos el primer documento del resultado:
        String docname = "";
        if (resul != null) {
            String docid = resul.get(resul.size()-1).getDocId();
            ModuloNombre doc = ps.indexReader.getDiccionarioDocs_NM().get(docid);
            docname = doc.getNombre();
            System.out.println("\nLa búsqueda tiene su mejor resultado en el documento: " + docname);
        } else {
            System.out.println("No hay resultados de la búsqueda");
            return;
        }

        //Dividimos la query en tokens                        
        StringTokenizer tokens = new StringTokenizer(query, Utils.DefaultSeparators);
        ArrayList<String> lq = new ArrayList<String>();
        while (tokens.hasMoreTokens()) {
            //Por cada término de la query...
            String termino = tokens.nextToken();
            termino = termino.toLowerCase();
            lq.add(termino);
        }

        // buffer para guardar lectura del fichero
        byte[] buffer = new byte[2048];

        //Acceso al zip con los documentos
        InputStream theFile;

        try {
            theFile = new FileInputStream(indexDir + Utils.STR_DEFAULT_ZIPNAME);

            ZipInputStream stream = new ZipInputStream(theFile);

            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                //Si damos con el documento de la respuesta buscamos la query en su interior
                if (entry.getName().equals(docname)) {
                    long len = 0; //longitud en bytes de texto leído del documento actual
                    String value, texto = "";

                    //Obtenemos el texto en bruto del fichero:
                    while ((len = stream.read(buffer)) > 0) {
                        value = new String(buffer, 0, (int) len, "UTF-8");
                        texto = texto.concat(value);
                    }

                    //parseamos el texto
                    TextParser parser = new HTMLSimpleParser();;
                    if (parser != null) {
                        texto = parser.parse(texto);
                    }

                    StringTokenizer tokens2 = new StringTokenizer(texto, Utils.DefaultSeparators); //PREGUNTAR SI ESTAN BIEN ESTOS SEPARADORES
                    int maxlong = 0;
                    int min = 0;
                    int max = 0;
                    
                    while (tokens2.hasMoreTokens()) {
                        String termino = tokens2.nextToken();
                        int pos = texto.indexOf(termino);
                        termino = termino.toLowerCase();

                        if (lq.contains(termino)) {
                            lq.remove(termino);
                            
                            //System.out.println(termino+" :"+pos);
                            
                            if (pos > max) {
                                max = pos;
                                maxlong = termino.length();
                            }
                            if ((pos < min) || (min == 0)) {
                                min = pos;
                            }
                            if(lq.size()==0){
                                break;
                            }
                        }
                    }

                    System.out.println("Mostramos el trozo del texto en el que aparece:");
                    //System.out.println(texto);
                    //System.out.println(min + "----" + max + "----" + maxlong);
                    if((min>=20)&&((max+maxlong)<=texto.length()-1)){
                        System.out.println(texto.substring(min-20, max + maxlong+20));
                    }else{
                        System.out.println(texto.substring(min, max + maxlong));
                    }
                    
                    return;
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        /*
         String termino = "a";
         String posting_debug = termino+": ";
         for (Posting p : ir.getTermPostings(termino)) {
         posting_debug = posting_debug + Utils.ExternPostingSeparator + String.valueOf(p.getDocId()) + Utils.InternPostingSeparator + String.valueOf(p.getNumTerms());
         for (Long l : p.getTermPositions()) {
         posting_debug = posting_debug + Utils.InternPostingSeparator + String.valueOf(l);
         }
         }
         System.out.println(posting_debug);
         */
    }

    public BasicReader getIndexReader() {
        return this.indexReader;
    }

}
