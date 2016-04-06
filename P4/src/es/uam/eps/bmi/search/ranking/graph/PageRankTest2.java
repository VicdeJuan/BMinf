package es.uam.eps.bmi.search.ranking.graph;

import org.ejml.alg.dense.mult.MatrixMatrixMult;
import org.ejml.data.DenseMatrix64F;


public class PageRankTest2 {
    
    private final static String fileOfLinks = "colecciones/pageRank/test2";
    private final static int size = 5;
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
