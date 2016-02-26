
0) El índice debería guardar el módulo del documento.


1) Para escribir el índice, utilizar:
## Correcci�n 26/02 - de Dejuan.
Los t�rminos no tienen m�dulo, asique los quitamos que no tiene sentido.

	linea: termino ESPACIO lista_de_postings

	lista_de_postings: posting
					| posting ESPACIO lista_de_postings

	posting: docId COMA NumPos COMA posiciones

	posiciones:   long
				| long COMA posiciones

Por ejemplo:

Parra 1,3,1,2,3 2,2,1,2
termino lista_de_postings_del_doc_1 lista_de_postings_del_doc_2



Utilizar comas para separar las posiciones y espacios para separar lo demás, hace que podamos leer del índice se puede hacer:

	String[] S = Linea.split(" "));
	termino = S[0]
	modulo = S[1]
	for (String posting : S[2..end]){
		Long [] postings = posting.split(",");
		docId = posting[0]
		frecuencia = posting[1]
		for (Long posicion : posting[2..end]){

		}
	}

PD: donde pone coma y espacio, lo ideal sería tener una variable global en Utils.java para que no haya problemas de sincronización y podamos cambiarlo fácilmente.


2) RandomAccesFile para leer el fichero. Tiene fseek que da mucha agilidad

3) Diccionario: {termino -> offset en el indice} (para poder hacer fseek).

	Este diccionario debería escribirse durante la creación del índice y al leer el índice, lo cargaremos en memoria en la clase BasicReader
		(ya que si cambiamos la manera de leer y prescindimos de este fichero, el BasicSearcher no debería verse afectado)

4) Diccionario: {idDocumento -> nombre del documento} (este ya está hecho)
