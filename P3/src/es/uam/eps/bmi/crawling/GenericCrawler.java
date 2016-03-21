/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.crawling;

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

/**
 *
 * @author dani
 */
public class GenericCrawler {

    int NUMPAGES;
    String OUTPATHFOLDER;
    List<String> domains;
    private Set<String> pagesVisited;

    public GenericCrawler(int NUMPAGES, String OUTPATHFOLDER, List<String> domains) {
        this.NUMPAGES = NUMPAGES;
        this.OUTPATHFOLDER = OUTPATHFOLDER;
        this.domains = domains;
        this.pagesVisited = new HashSet<String>();
    }

    public static void main(String[] args) throws IOException {
        URL url;
        File f;

        GenericCrawler crawler;
        int numpages = 100;
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
            crawl(url, crawler);
        }
    }

    /**
     *
     * @param url
     */
    public static void crawl(URL url, GenericCrawler crawler) throws IOException {

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
        FileWriter graph = new FileWriter("grafh.txt");
        PrintWriter pw = null;

        BufferedWriter grafo = new BufferedWriter(graph);
        pagesVisited.add(url.toString());
        crawler.setPagesVisited(pagesVisited);
        //Escribo las salientes de la primera        
        pw = new PrintWriter(graph);
        pw.println(url + " " + urlstovisit.size());
        for (String prin : urlstovisit) {
            pw.print(prin + " ");
        }
        pagina++;
        /*Para cada paginas que tengo que visitar hago todo*/

        for (int k = 0; k < urlstovisit.size(); k++) {
            if (k < crawler.getNUMPAGES() - 1) {

                String tovisit = urlstovisit.get(k);
                List<String> urlstovisitaux = new ArrayList<>();

                if (!pagesVisited.contains(tovisit)) {

                    URL nuevaUrl = new URL(tovisit);
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(nuevaUrl.openStream()));
                    BufferedWriter writer1 = new BufferedWriter(new FileWriter(path + "/" + pagina + ".html"));
                    urlstovisitaux = retwebpages(reader1, writer1, nuevaUrl);
                    urlstovisit.addAll(urlstovisitaux);

                }
                pagina++;

                pw.println(url + " " + urlstovisit.size());
                for (String prin : urlstovisit) {
                    pw.print(prin + " ");
                }
                pagesVisited.add(tovisit);
            } else {
                return;
            }
        }

        graph.close();

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
                    urll = aux[1].split("\"")[1];
                //System.out.println(urll);
                    //Hay que ver porque no funcionan las webs externas
                    //Aunque como pone en el enunciado que se restrinja a un unico dominio, no seria necesario.
                    //urls.add(urll);

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
}
