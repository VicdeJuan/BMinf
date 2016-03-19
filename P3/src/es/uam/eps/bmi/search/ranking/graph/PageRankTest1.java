package es.uam.eps.bmi.search.ranking.graph;

import java.util.Arrays;


public class PageRankTest1 {
	
	public static void main(String[] argv){
		PageRank pg = new PageRank("colecciones/PageRank/test1",4);
		
		System.out.println(pg);
		for (double d : pg.iterate(15).getRow(0))
			System.out.println(d);
	}
	}

