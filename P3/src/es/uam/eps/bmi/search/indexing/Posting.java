package es.uam.eps.bmi.search.indexing;

import java.util.ArrayList;
import java.util.List;

public class Posting {

   

	private String docId;
	private List<Long> termPositions;

	/**
	 * Crea un posting nuevo para un termino, en un documento con su lista
	 * de posiciones.
	 *
	 * termino dentro del documento.
	 */
        
        public Posting(){
            this.termPositions = new ArrayList<>();
        }
        
	public Posting(String docId, List<Long> termPositions) {
            this.docId = docId;
            this.termPositions = termPositions;

	}

	/**
	 * Getter del DocId.
	 *
	 * @return
	 */
	public String getDocId() {
		return this.docId;
	}
        
      

	/**
	 *
	 * @return La frecuencia con la que el termino del posting aparece en el
	 * documento.
	 */
	public int getTermFrequency() {
		return this.termPositions.size();
	}

	/**
	 *
	 * @return Las posiciones del término en el documento del posting.
	 */
	public List<Long> getTermPositions() {
		return this.termPositions;
	}
        
        /**
	 *
	 * @return Las posiciones del término en el documento del posting.
	 */
	public Long getOneTermPosition_Int(int index) {
		return this.termPositions.get(index);
	}
        
        
        /**
	 *
	 * @return Las posiciones del término en el documento del posting.
	 */
	public String getOneTermPosition_String(int index) {
		return String.valueOf(this.termPositions.get(index));
	}
        
        
        /**
	 * Añade una posición del término.
	 *
	 * @param pos posicion que añadir.
	 */
	public void addTermPosition(Long pos) {
		this.termPositions.add(pos);
	}
        
        /**
	 * Añade una posición del término.
	 *
	 * @param pos posicion que añadir.
	 */
	public void deleteTermPosition(Long pos) {
		this.termPositions.remove(pos);
	}
        
        
        
        /**
	 * Añade una posición del término.
	 *
         * @return Numero de temrinos
	 */
	public int getNumTerms() {
		return this.termPositions.size();
	}
        
    public static Posting mezclarPostings(Posting p1, Posting p2){
        String docId = p1.getDocId();
        String numTerms = String.valueOf(p1.getNumTerms() + p2.getNumTerms());    
        List<Long> l = new ArrayList<Long>();
        
        Posting p = new Posting(docId, l);
        
        for(Long l1 : p1.getTermPositions()){
            for(Long l2 : p2.getTermPositions()){
                if(l1 < l2 ){
                    p.addTermPosition(l1);
                    break;
                }else if(l1 > l2){
                    p.addTermPosition(l2);
                    p2.deleteTermPosition(l2);
                    break;
                }else{
                    System.out.println("BIG PROBLEM");
                }
            }
        }
        
        for(Long l2 : p2.getTermPositions()){
            p.addTermPosition(l2);
            p2.deleteTermPosition(l2);
        }
               
        return p; 
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setTermPositions(List<Long> termPositions) {
        this.termPositions = termPositions;
    }

    //Devuelvo las posiciones del posterior
    public List<Long> posicionesLiteral(Posting posterior){
        List<Long> toret=new ArrayList();
        for(long pos1:this.getTermPositions()){
            for(long pos2:posterior.getTermPositions()){
                if(pos2==pos1+1){
                //hay match
                toret.add(pos2);
                break;
                }
            }
        }
        return toret;
    }
        
}
