package es.uam.eps.bmi.search.ranking.graph;

import org.ejml.alg.dense.mult.MatrixMatrixMult;
import org.ejml.data.DenseMatrix64F;


public class PageRankTest2 {
    
    private final static String fileOfLinks = "colecciones/pageRank/test2";
    private final static int size = 5;
    
    public static void main(String[] argv){
        		PageRank pg = new PageRank(fileOfLinks,size);
		System.out.print(pg);
 		for (double d : pg.iterate(15).getRow(0))
			System.out.println(d);
    }
    
}
