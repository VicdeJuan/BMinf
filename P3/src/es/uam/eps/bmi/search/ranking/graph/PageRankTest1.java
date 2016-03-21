package es.uam.eps.bmi.search.ranking.graph;

import java.util.Arrays;
import java.util.stream.DoubleStream;


public class PageRankTest1 {
	
	private final static String fileOfLinks = "colecciones/pageRank/test1";
	private final static int size = 4;
	private static double r = 0.1;
	
	public static void main(String[] argv){
		PageRank pg;
		pg = new PageRank(fileOfLinks,size,r);

		System.out.println(pg.iterate(0.005,600));
		pg.calculateScores();
		for (double d : pg.getScores())
			System.out.println(d);
	}
	}

