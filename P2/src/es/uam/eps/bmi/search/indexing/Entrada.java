package es.uam.eps.bmi.search.indexing;

import java.util.ArrayList;
import java.util.HashMap;


public class Entrada {
    private String termino;
    private ArrayList<Posting> listaPostings;
    private HashMap diccionarioPostings; //(docId, posicion array listaPostings)
    
    public Entrada(String termino){
        this.termino = termino;
        this.listaPostings = new ArrayList<Posting>();
        this.diccionarioPostings = new HashMap();
    }

    public Entrada(String termino, ArrayList<Posting> listaPostings) {
        this.termino = termino;
        this.listaPostings = listaPostings;
        this.diccionarioPostings = new HashMap();
    }
    
    public void addPosting (Posting p){
        this.listaPostings.add(p);
        this.diccionarioPostings.put(p.getDocId(), this.listaPostings.indexOf(p));
    }
    
    public Posting getPosting (String docId){
        Integer pos = (Integer) this.diccionarioPostings.get(docId);
        
        if (pos==null){
            return null;
        }
        return this.listaPostings.get(pos);
    }
    
    public void eliminarPosting (Posting p){
        this.diccionarioPostings.remove(p.getDocId());
        //this.listaPostings.remove(p);
    }
    
    
    public static Entrada mezclarEntradas(Entrada e1, Entrada e2){
        Entrada e = new Entrada(e1.getTermino());
        Long docId;
        
        for(Posting p1 : e1.listaPostings){
            docId = Long.parseLong(p1.getDocId());
            for(Posting p2 : e2.listaPostings){
                if(docId == Long.parseLong(p2.getDocId())){
                    e.addPosting(Posting.mezclarPostings(p1, p2));
                    e2.eliminarPosting(p2);
                    break;
                }else if(docId < Long.parseLong(p2.getDocId())){
                    e.addPosting(p1);
                    break;
                }else{
                    e.addPosting(p2);
                    e2.eliminarPosting(p2);
                }
            }
        }
        
        for(Posting p2 : e2.listaPostings){
            e.addPosting(p2);
            e2.eliminarPosting(p2);
        }
        
        return e;
    }
 
    
    
    
    
    
    public void setTermino(String termino) {
        this.termino = termino;
    }

    public void setListaPostings(ArrayList<Posting> listaPostings) {
        this.listaPostings = listaPostings;
    }

    public String getTermino() {
        return termino;
    }

    public ArrayList<Posting> getListaPostings() {
        return listaPostings;
    }
    
    
}
