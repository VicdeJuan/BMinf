/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.ranking.graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

public class PageRankTest3 {

	private final static String fileOfLinks = "colecciones/pageRank/links_1K.txt";
	private static int size;
	private static final double r = 0.1;

	public static void main(String[] argv) {
		String line;
		size = 0;
		try {
			FileReader fr = new FileReader(fileOfLinks);
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				size++;
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(PageRankTest3.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(PageRankTest3.class.getName()).log(Level.SEVERE, null, ex);
		}

		PageRank pg;
		pg = new PageRank(fileOfLinks, size, r);

		pg.calculateScores();
		System.out.println(DoubleStream.of(pg.getScores()).max());

		pg.writeValues();

	}
}
