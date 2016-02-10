
package es.uam.eps.bmi.search;

/**
 *
 * @author e267044
 */
public class TextDocument {

     
    String id;
    String name;
    
    /**
     * Crea un nuevo Textdocument.
     * @param id    Id del documento a crear.
     * @param name  Nombre (path completo) del documento a crear.
     */
    public TextDocument(String id, String name){
        this.id = new String(id);
        this.name = new String(name);
    }
    
    /** Devolverá un identificador único de documento.
     *
     * @return
     */
    public String getId(){
	return id;
    }


    /** Devolverá el nombre (path completo) del documento.
     *
     * @return
     */
    public String getName(){
	return name;
    }
    
    /*Sobreescribirá el método equals de Object comparando identificadores de documentos.*/
    @Override
    public boolean equals(Object object){
	return this.getId().equals(((TextDocument) object).getId());
    }


    /*Sobreescribirá l método hashCode de Object devolviendo el código hash del identificador del documento.*/
    @Override
    public int hashCode(){
	return this.getId().hashCode();
}

}
