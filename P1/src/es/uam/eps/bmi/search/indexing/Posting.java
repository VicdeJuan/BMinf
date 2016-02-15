/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.indexing;

import java.util.List;

/**
 *
 * @author e267044
 */
public class Posting {
    private String docId;
    private String term;
    private List<Long> termPositions;
    private int freq;

    public Posting(String docId, String term, List<Long> termPositions) {
        this.docId = docId;
        this.term = term;
        this.termPositions = termPositions;
	this.freq = 0;
    }

    
   String getDocId(){
   return this.docId;
   }
/* que
devolverá el identificador de un documento donde aparece el término asociado al 
posting
*/
int getTermFrequency(){
 return this.termPositions.size();
}
/*
que  devolverá  el  número  de 
veces  que
el  término 
aparece 
en  el  documento 
asociado al 
posting
*/
List<Long> getTermPositions(){
return this.termPositions;
}
/*
que devolverá las posiciones del término en el documento del 
posting*/

}
