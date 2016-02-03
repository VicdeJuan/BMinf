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
public class TextDocument {
     //Devolverá un identificador único de documento.
public String getId(){
	return null;
}
//Devolverá el nombre (path completo) del documento.
public String getName(){
	return null;
}
//Sobreescribirá el método equals de Object comparando identificadores de documentos.
public boolean equals(Object object){
	return false;
}


//Sobreescribirá l método hashCode de Object devolviendo el código hash del identificador del documento.
public int hashCode(){
	return 0;
}
}
