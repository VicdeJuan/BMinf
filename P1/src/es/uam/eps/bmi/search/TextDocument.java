package es.uam.eps.bmi.search;

public class TextDocument {

	private String id;
	private String name;

	public TextDocument(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Devolverá un identificador único de documento.
	 *
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Devolverá el nombre (path completo) del documento.
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	/*Sobreescribirá el método equals de Object comparando identificadores de documentos.*/

	public boolean equals(Object object) {
		return object.equals(this.id);

	}


	/*Sobreescribirá l método hashCode de Object devolviendo el código hash del identificador del documento.*/
	public int hashCode() {
		return this.id.hashCode();
	}

}
