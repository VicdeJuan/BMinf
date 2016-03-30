package es.uam.eps.bmi.search.indexing;

import java.util.ArrayList;
import java.util.HashMap;


public class Indice {
    private HashMap diccionarioTerminos; //(termino, posicion array listaEntradas)
    private ArrayList<Entrada> listaEntradas;
    
    public Indice(ArrayList<Entrada> listaEntradas) {
        this.diccionarioTerminos = new HashMap();
        this.listaEntradas = listaEntradas;
    }
    
    
    public boolean contieneTermino(String termino){
        return this.diccionarioTerminos.containsKey(termino);
    }
    
   
    public void addEntrada(Entrada e){
        this.listaEntradas.add(e);
        this.diccionarioTerminos.put(e.getTermino(), this.listaEntradas.indexOf(e));
    }
    
    public void addPostingToEntrada(Posting p, String termino){
        
    }
    
    public Entrada getEntrada(String termino){
        Integer pos = (Integer) this.diccionarioTerminos.get(termino);
        
        if (pos==null){
            return null;
        }
        return this.listaEntradas.get(pos);
    }
    
    public int getNumEntradas(){
        return this.listaEntradas.size();
    }
    
    public void eliminarEntrada(Entrada e){
        this.diccionarioTerminos.remove(e.getTermino());
        //this.listaEntradas.remove(e);
    }

    public ArrayList<Entrada> getListaEntradas() {
        return listaEntradas;
    }

    public void setListaEntradas(ArrayList<Entrada> listaEntradas) {
        this.listaEntradas = listaEntradas;
    }
    
    

    
    
    
    
    
}
