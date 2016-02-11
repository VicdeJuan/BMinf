/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search;

/**
 *
 * @author e267044
 */
public class ScoredTextDocument implements java.lang.Comparable {
private String docId;
private double score;

    public ScoredTextDocument(String docId, double score) {
        this.docId = docId;
        this.score = score;
    }


    public int compareTo(ScoredTextDocument o) {
        return  (int) (this.score - o.getScore());
    }
    String getDocId(){
    return this.docId;
    }
/*que
devolverá el identificador del documento asociado al resultado
*/
double getScore(){
return this.score;
}
/* que
devolverá el 
score
asociado al resultado
*/

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
