package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.recommend.FilterCallables.FilterCallableUser;
import es.uam.eps.bmi.recommend.FilterCallables.FilterCallableItem;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import es.uam.eps.bmi.search.ranking.graph.PageRank;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase abstracta necesaria para que quien herede de recomender tenga los
 * mismos métodos. Así evitamos repetición de código.
 *
 * Si Java dejara implementar métodos en una interfaz, esto no haría falta. Pero
 * Java no lo permite.
 *
 */
public abstract class RecommenderAbs implements Recommender {

    /**
     * Matriz del recomendador. Típicamente será con los usuarios como filas y
     * los items como columnas
     */
    public Matrix matriz = null;
    int numUser = 0;
    int numItem = 0;

    /**
     * Lee el fichero y construye la matriz. Este método tiene bastante enjundia
     * para poder ser utilizado por todos los recomendadores que hagamos.
     *
     * Por otro lado, cada fichero tiene una estructura distinta, distintos
     * campos, etc... Por eso, según el tipo de fichero que sea (determinado por
     * flagFileType) se llamará a un parser u otro. Estos parsers son las clases
     * FilterCallable*.java.
     *
     *
     *
     * @param fichero	Fichero de donde leer la información.
     * @param mat	Matriz a rellenar de datos. Hay que pasarla creada pero vacía.
     * @param flagFileType Tipo de fichero a procesar. Cada tipo tiene asociado
     * un valor en su correspondiente FilterCallable. Para más información,
     * consultar FilterCallableItem.java.
     *
     * @param RealIDtoIdx	Dado que los id's en el fichero pueden no estar
     * ordenados de 1 a n (como es el caso de movies.dat que empiezan en 75),
     * necesitamos almacenar que el id 75 se corresponde con la primera columna
     * de la matriz. Para esto está el LinkedHashMap.
     *
     * @param fillCols	El fichero puede interesar leerlo como matriz normal o
     * como matriz traspuesta. Para entender esto, fijarse en el ejemplo de las
     * transparencias de Rocchio y en los ficheros CRTItem.dat y CRTUser.dat. En
     * las transparencias el la matriz de usuarios tiene los usuarios como filas
     * y el fichero CRTUser.dat tiene los usuarios como filas, entonces vamos a
     * rellenar la matriz fila a fila, con lo que el argumento fillCols es
     * false. Por el contrario, el fichero CRTItem.dat tiene los items en las
     * filas, pero nos interesa tenerlos en las columnas, como se ve en el
     * ejemplo. Por ello, rellenamos la matriz por columnas, es decir, el
     * argumento fillCols es True. Para más información, ver las llamadas en
     * ContentsRocchioTest.java.
     */
    public void CargarMatrizPorLineas(String fichero, Matrix mat, int flagFileType, LinkedHashMap<Integer, Integer> RealIDtoIdx, boolean fillCols) {

        if (numItem == 0 || numUser == 0) {
            System.err.println("Tenemos un problema si el número de items es 0. Hay que setear su valor con anterioridad");
            System.exit(-1);
        }
        // Inicialización de variables		
        FileReader fr = null;
        int row = numUser, col = numItem, counter = 0;
        String doc;
        double[] tofill = null;

        try {
            fr = new FileReader(fichero);

            BufferedReader br = new BufferedReader(fr);

            String line;

            /**
             * Este bucle lee el fichero linea a linea. Para cada linea, la
             * procesa y rellena la matriz que hemos dado como argumento.
             * Procesa la línea según lo especificado por argumento por
             * flagFileType, llamando a un filterCallable o a otro.
             */
            int paso = 0;
            while ((line = br.readLine()) != null) {

                // Creación del vector a ser rellenado por FilterCallable*.java
                tofill = new double[mat.getNumCols()];
                // Id del elemento que procesamos en la línea.
                int id;
                //metemos una flag para comernos la cabecera
                boolean flag = true;

                // Añadir el resto de posibles filtros dependiendo del fichero
                if (flagFileType == FilterCallableUser.CODE) {
                    id = (new FilterCallableUser(tofill, line)).call();
                } else if (flagFileType == FilterCallableItem.CODE) {
                    id = (new FilterCallableItem(tofill, line)).call();
                } else {
                    id = -1;
                }

                // Para llevar registro en caso de no tener todos los ids en el fichero.
                RealIDtoIdx.putIfAbsent(id, counter);
                paso++;
                // Si rellenamos la matriz por columnas o por filas.
                if (fillCols) {
                    mat.setCol(tofill, counter);
                } else {
                    mat.setRow(tofill, counter);
                }
                counter++;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("No se ha encontrado el archivo " + fichero);
        } catch (IOException ex) {
            Logger.getLogger(PageRank.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("IOException al cargar " + fichero);
        } catch (Exception ex) {
            Logger.getLogger(RecommenderAbs.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error al parsear " + fichero + "(lin,col) = (" + row + "," + col + ")");
        }
    }

    /**
     * Setters & getters
     */
    public void setNumUser(int numUser) {
        this.numUser = numUser;
    }

    public void setNumItem(Integer numItem) {
        this.numItem = numItem;
    }

    /**
     * Carga la matriz de datos del fichero a partir de varios parámetros
     * necesarios.
     *
     * @param fileofContents	Fichero donde se encuentra la información.
     * @param rowsIdx	Columna del fichero (empezando a numerar en 0) que vamos a
     * tomar como filas de la matriz (típicamente serán los usuarios).
     * @param colsIdx	Columna del fichero (empezando a numerar en 0) que vamos a
     * tomar como columnas de la matriz (típicamente serán los items vistos)
     * @param ratingsIdx	Columna del fichero (empezando a numerar en 0) que
     * vamos a tomar como valor a insertar en la matriz.
     * @param IdxToId_row	HashMap de Ids de lo que vaya a actuar de filas
     * (típicamente usuarios). De esta manera podemos trabajar datos de usuarios
     * cuyos id's empiecen en un número que no sea 0, que haya saltos, etc.
     * @param IdxToId_col	HashMap de Ids de lo que vaya a actuar de columnas. De
     * esta manera podemos trabajar datos de usuarios cuyos id's empiecen en un
     * número que no sea 0, que haya saltos, etc.
     * @param ignoreLines	Número de líneas que van a ser ignoradas del principio
     * dle fichero (típicamente las cabeceras)
     * @return
     */
    protected Matrix cargarMatrizString(String fileofContents, int rowsIdx, int colsIdx, int ratingsIdx,
            LinkedHashMap<Integer, Integer> IdxToId_row, LinkedHashMap<Integer, Integer> IdxToId_col, int ignoreLines) {
        return this.cargarMatriz(fileofContents, rowsIdx, colsIdx, ratingsIdx, IdxToId_row, IdxToId_col, 50, 50, ignoreLines);

    }

    /**
     * Carga la matriz de datos del fichero a partir de varios parámetros
     * necesarios.
     *
     * @param fileofContents	Fichero donde se encuentra la información.
     * @param rowsIdx	Columna del fichero (empezando a numerar en 0) que vamos a
     * tomar como filas de la matriz (típicamente serán los usuarios).
     * @param colsIdx	Columna del fichero (empezando a numerar en 0) que vamos a
     * tomar como columnas de la matriz (típicamente serán los items vistos)
     * @param ratingsIdx	Columna del fichero (empezando a numerar en 0) que
     * vamos a tomar como valor a insertar en la matriz.
     * @param IdxToId_row	HashMap de Ids de lo que vaya a actuar de filas
     * (típicamente usuarios). De esta manera podemos trabajar datos de usuarios
     * cuyos id's empiecen en un número que no sea 0, que haya saltos, etc.
     * @param IdxToId_col	HashMap de Ids de lo que vaya a actuar de columnas. De
     * esta manera podemos trabajar datos de usuarios cuyos id's empiecen en un
     * número que no sea 0, que haya saltos, etc.
     * @param rowStep	Parámetro de realloc dinámico de las filas de las
     * matrices. Valores altos implican desperdicio de memoria, valores bajos
     * implican poca eficiencia.
     * @param colStep	Parámetro de realloc dinámico de las columnas de las
     * matrices. Valores altos implican desperdicio de memoria, valores bajos
     * implican poca eficiencia.
     * @param ignoreLines	Número de líneas que van a ser ignoradas del principio
     * dle fichero (típicamente las cabeceras)
     * @return
     */
    protected Matrix cargarMatriz(String fileofContents, int rowsIdx, int colsIdx, int ratingsIdx,
            LinkedHashMap<Integer, Integer> IdxToId_row, LinkedHashMap<Integer, Integer> IdxToId_col, int rowStep, int colStep,
            int ignoreLines) {
        FileReader fr = null;
        int row, col, counterRow = 0, counterCol = 0;

        if (IdxToId_row == null) {
            IdxToId_row = new LinkedHashMap<>();
        } else {
            counterRow = IdxToId_row.size();
        }
        if (IdxToId_col == null) {
            IdxToId_col = new LinkedHashMap<>();
        } else {
            counterCol = IdxToId_col.size();
        }

        Matrix matToRet = new Matrix(Math.max(rowStep, counterRow), Math.max(counterCol, colStep));
        double rating;
        String[] values;

        try {
            fr = new FileReader(fileofContents);

            BufferedReader br = new BufferedReader(fr);
            String line;

            //Ignoramos las líneas.
            for (int i = 0; i < ignoreLines; i++) {
                br.readLine();
            }

            /**
             * Este bucle lee el fichero linea a linea. Para cada linea, la
             * procesa y rellena la matriz que hemos dado como argumento.
             * Procesa la línea según lo especificado por argumento por
             * flagFileType, llamando a un filterCallable o a otro.
             */
            while ((line = br.readLine()) != null) {
                values = line.split("\t");

                row = Integer.parseInt(values[rowsIdx]);
                col = Integer.parseInt(values[colsIdx]);
                rating = Double.parseDouble(values[ratingsIdx]);
                // Para llevar registro en caso de no tener todos los ids en el fichero.
                if (!IdxToId_row.containsKey(row)) {
                    IdxToId_row.put(row, counterRow);
                    counterRow++;

                    if (counterRow % rowStep == 0 || counterRow > matToRet.getNumRows()) {
                        System.out.println(String.format("Aumentando filas por %d...", counterRow / rowStep));
                        matToRet.addRowLast(rowStep);
                    }
                }
                if (!IdxToId_col.containsKey(col)) {
                    IdxToId_col.put(col, counterCol);
                    counterCol++;

                    if (counterCol % colStep == 0 || counterCol > matToRet.getNumCols()) {
                        System.out.println(String.format("Aumentando columnas por %d...", counterCol / colStep));
                        matToRet.addColLast(colStep);
                    }

                }
                // Si rellenamos la matriz por columnas o por filas.
                int r = IdxToId_row.get(row);
                int c = IdxToId_col.get(col);
                matToRet.set(r, c, rating);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            System.err.println("No se ha encontrado el archivo " + fileofContents);
        } catch (IOException ex) {
            System.err.println("IOException  " + fileofContents);
        } catch (NumberFormatException ex) {
            System.err.println("Fichero mal formado" + fileofContents);

        }

        return matToRet;
    }

    public HashMap<Integer, String> CargarColumnasFichero(String fichero, int id, int columna) throws FileNotFoundException, IOException {

        HashMap<Integer, String> pelis = new HashMap();
        String cadena;

        FileReader f = new FileReader(fichero);
        BufferedReader b = new BufferedReader(f);
        //me quito la primera linea de nombres
        cadena = b.readLine();
        while ((cadena = b.readLine()) != null) {
            String[] split = cadena.split("\t");
            int movieid = Integer.parseInt(split[id]);
            String nombrePeli = split[columna];
            pelis.put(movieid, nombrePeli);
        }
        b.close();

        return pelis;
    }

}
