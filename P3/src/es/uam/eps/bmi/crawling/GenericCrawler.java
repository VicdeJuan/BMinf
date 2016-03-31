/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.crawling;

import es.uam.eps.bmi.search.ScoredTextDocument;
import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.indexing.BasicIndex;
import es.uam.eps.bmi.search.parsing.HTMLSimpleParser;
import es.uam.eps.bmi.search.parsing.QueryParser;
import es.uam.eps.bmi.search.parsing.TextParser;
import es.uam.eps.bmi.search.ranking.graph.PageRank;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import es.uam.eps.bmi.search.parsing.XMLReader;
import es.uam.eps.bmi.search.searching.ModuloNombre;
import es.uam.eps.bmi.search.searching.TFIDFSearcher;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author dani
 */
public class GenericCrawler {

    int NUMPAGES;
    String OUTPATHFOLDER;
    List<String> domains;
    private Set<String> pagesVisited;
    double r;

    public GenericCrawler(int NUMPAGES, String OUTPATHFOLDER, List<String> domains) {
        this.NUMPAGES = NUMPAGES;
        this.OUTPATHFOLDER = OUTPATHFOLDER;
        this.domains = domains;
        this.pagesVisited = new HashSet<String>();
        this.r = 0.1;
    }

    public static void main(String[] args) throws IOException {
        URL url;
        File f;
        String outfile = "graph.txt";

        GenericCrawler crawler;
        int numpages = 10;
        String outfolder = "WEBCRAWLER";
        List<String> dom = new ArrayList<>();
        dom.add("https://es.wikipedia.org/wiki/Miner%C3%ADa_de_datos");
        crawler = new GenericCrawler(numpages, outfolder, dom);

        // creando directorio
        f = new File(outfolder);
        f.mkdir();

        //le paso la primera url
        for (String paginas_dominio : dom) {
            url = new URL(paginas_dominio);
            crawl(url, crawler, outfile, numpages);
        }
    }

    /**
     *
     * @param url
     */
    public static void crawl(URL url, GenericCrawler crawler, String outfile, int numpages) throws IOException {

        List<String> urlstovisit = new ArrayList<>();
        String path = crawler.getOUTPATHFOLDER();
        BufferedReader br;
        Set<String> pagesVisited = new HashSet<String>();
        int pagina = 1;

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + pagina + ".html"));
        urlstovisit = retwebpages(reader, writer, url);

        reader.close();
        writer.close();
        // Ya tengo todas las URL de la primera página
        FileWriter graph = new FileWriter(outfile);
        PrintWriter pw = null;

        BufferedWriter grafo = new BufferedWriter(graph);
        pagesVisited.add(url.toString());
        crawler.setPagesVisited(pagesVisited);
        //Escribo las salientes de la primera        
        pw = new PrintWriter(graph);
        pw.print(url + " " + urlstovisit.size() + " ");
        for (String prin : urlstovisit) {
            pw.print(prin + " ");
        }
        pw.println();
        pagina++;
        /*Para cada paginas que tengo que visitar hago todo*/

        for (int k = 0; k < urlstovisit.size(); k++) {
            if (k < crawler.getNUMPAGES() - 1) {

                String tovisit = urlstovisit.get(k);
                List<String> urlstovisitaux = new ArrayList<>();

                if (!pagesVisited.contains(tovisit)) {

                    URL nuevaUrl = new URL(tovisit);
                    try {
                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(nuevaUrl.openStream()));
                        BufferedWriter writer1 = new BufferedWriter(new FileWriter(path + "/" + pagina + ".html"));
                        urlstovisitaux = retwebpages(reader1, writer1, nuevaUrl);
                        urlstovisit.addAll(urlstovisitaux);
                    } catch (Exception e) {

                        break;
                    }

                }
                pagina++;

                pw.print(url + " " + urlstovisit.size() + " ");
                for (String prin : urlstovisit) {
                    pw.print(prin + " ");
                }
                pw.println();
                pagesVisited.add(tovisit);
            } else {
                break;
            }
        }
        Set<String> auxvisit = crawler.getPagesVisited();
        auxvisit.addAll(pagesVisited);
        crawler.setPagesVisited(auxvisit);
        graph.close();

        //Ya se genera todo lo necesario ahora hay que inicializar el indice y cargar pagerank
        PageRank pg;
        String grafotxt = "graph.txt";
        /*Vamos a ver cuantas webs hay en graph*/
        FileReader f = new FileReader(grafotxt);
        BufferedReader b = new BufferedReader(f);
        String cadena;
        HashSet<String> docsunicos = new HashSet<>();
        while ((cadena = b.readLine()) != null) {
            String[] split = cadena.split(" ");
            docsunicos.add(split[0]);
            //me quito el uno que me da cuantas hay
            for (int j = 2; j < split.length; j++) {
                docsunicos.add(split[j]);
            }
        }
        b.close();
        int ndocsunicos = docsunicos.size();

        /*Tenemos que comprimir los html para generar un docs.zip*/
        boolean flag = true;
        int n = 1;

        FileOutputStream out = new FileOutputStream("docs.zip");
        ZipOutputStream zipOut = new ZipOutputStream(out);
        /*
         String inputFile = "WEBCRAWLER/"+n+".html";
         FileInputStream in = new FileInputStream(inputFile);
         ZipEntry entry = new ZipEntry(inputFile);
         zipOut.putNextEntry(entry);
        
         zipOut.close();
         */
String pathaux = "WEBCRAWLER";

        String files;
        File folder = new File(pathaux);
        File[] listOfFiles = folder.listFiles();
      /*  
        for (int i = 1; i <=listOfFiles.length; i++) {
            //System.out.println("WEBCRAWLER/"+listOfFiles[i-1].getName());
            String inputFile = "WEBCRAWLER/"+listOfFiles[i-1].getName() ;
            
            try {
                //FileInputStream in = new FileInputStream(inputFile);
                ZipEntry entry = new ZipEntry(inputFile);
                
                zipOut.putNextEntry(entry);
                zipOut.closeEntry();

            } catch (Exception e) {
                flag = false;
            }
            n++;
        }
        zipOut.close();
        */
   /*     
        //copiamos docs.zip a webcrawler
        File origen = new File("docs.zip");
        File destino = new File(pathaux+"/docs.zip");
        InputStream in = new FileInputStream(origen);
        OutputStream outp = new FileOutputStream(destino);
            byte[] buf = new byte[1024];
    int lent;
     
    while ((lent = in.read(buf)) > 0) {
      outp.write(buf, 0, lent);
    }

    in.close();
    outp.close();
        
        */
        
        

        pg = new PageRank(outfile, ndocsunicos, crawler.getR(), "pagerank.txt", "docs.zip");
        //System.out.println(pg.iterate(0.005,600));
        pg.calculateScores();
        for (double d : pg.getScores()) {
            //System.out.println(d);
        }
        pg.writeValues();
        /*Queremos mostrar el TOP 10 de PageRank*/

        //Para construir el indice
        TextParser parser = new HTMLSimpleParser();
        /*XMLReader xmlReader = new XMLReader("index-settings.xml");
         String indexDir = xmlReader.getTextValue(Utils.XMLTAG_INDEXFOLDER);
         String indexFile = indexDir + "/" + Utils.index_file;
         */
        String indexDir = "indice";
        BasicIndex basicIdx = new BasicIndex();

        //Si no existe, se crea
        //boolean build = !(new File(indexFile).exists());
        boolean build = true;
        if (build) {
            basicIdx.build(crawler.getOUTPATHFOLDER() + "/", indexDir, parser);
        } else {
            basicIdx.load(indexDir);
        }

        TFIDFSearcher tfSearch = new TFIDFSearcher();
        tfSearch.build(basicIdx);
        System.out.println("La busqueda es: Mineria de datos");

        String query = "datos";
        String[] querys = new QueryParser().parses(query);
        long len = 0;
        byte[] buffer = new byte[2048];

        InputStream theFile;
        List<ScoredTextDocument> resul = tfSearch.search(query);
        int topp = 10;
        if (resul.size() < 10) {
            topp = resul.size();
        }
        for (int i = 0; i < topp; i++) {
            String docidd = resul.get(i).getDocId();
            ModuloNombre doc = tfSearch.getIndice().getDiccionarioDocs_NM().get(docidd);
            String docname = doc.getNombre();
            System.out.println(docname);
        }

    }

    public static List<String> retwebpages(BufferedReader reader, BufferedWriter writer, URL url) throws IOException {
        List<String> urls = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            writer.write(line);
            writer.newLine();
            String urll;
            if (line.contains("href")) {
                String[] aux = line.split("href=");
                /* o bien cojo la url entera*/
                if (aux[1].contains("http:") || aux[1].contains("https:")) {

                    String[] split = aux[1].split("\"");

                    //Aunque como pone en el enunciado que se restrinja a un unico dominio, no seria necesario.
                    if (split.length > 1) {
                        urll = aux[1].split("\"")[1];
                        //urls.add(urll);
                        //System.out.println(urll);
                    } else {
                        break;
                    }

                } /*o bien si no tiene extension la uno a la anterior porque forma parte de la web*/ else {
                    if (!aux[1].contains(".")) {
                        urll = aux[1].split("\"")[1];
                        if (urll.contains("/")) {
                            //System.out.println(urll);
                            String fin;
                            fin = url.getProtocol() + "://" + url.getHost() + urll;
                            if (fin.contains("http:") || fin.contains("https:")) {
                                urls.add(fin);
                            }
                        }
                    }
                }
            }
        }
        return urls;
    }

    public int getNUMPAGES() {
        return NUMPAGES;
    }

    public String getOUTPATHFOLDER() {
        return OUTPATHFOLDER;
    }

    public List<String> getDomains() {
        return domains;
    }

    public Set<String> getPagesVisited() {
        return pagesVisited;
    }

    public void setOUTPATHFOLDER(String OUTPATHFOLDER) {
        this.OUTPATHFOLDER = OUTPATHFOLDER;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public void setPagesVisited(Set<String> pagesVisited) {
        this.pagesVisited = pagesVisited;
    }

    public double getR() {
        return r;
    }

}
