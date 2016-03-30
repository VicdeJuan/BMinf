/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.ranking.graph;

import es.uam.eps.bmi.search.Utils;
import java.util.stream.DoubleStream;

public class PageRankTest3 {

	private final static String CLUEWEB = "1K";
	private final static String fileOfLinks = "colecciones/pageRank/links_"+CLUEWEB+".txt";
	private final static String colections = "colecciones/clueweb-"+CLUEWEB+"/docs.zip";
	private final static String towrite = "colecciones/pageRank/page_rank_"+CLUEWEB+"";
	private static int size;
	private static final double r = 0.1;

	public static void main(String[] argv) {
		
		size = Utils.getSizeOfFile(fileOfLinks);


		PageRank pg;
		pg = new PageRank(fileOfLinks, size, r,towrite,colections);

		pg.calculateScores();
		System.out.println("Page rank máximo: " + DoubleStream.of(pg.getScores()).max());

		pg.writeValues();

	}
}
