package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContentsRocchio extends RecommenderAbs {

    public static void main(String[] argv) throws IOException {
/*
        String it = "data/CRTItem_2.dat";
        String us = "data/CRTUser_2.dat";
        // Row, Col, Value, Ignore (bis)
        ContentsRocchio instance = new ContentsRocchio(
                it, us, 0, 1, 2, 0, 0, 1, 2, 0, 5
        );*/
        String it = "data/movie_tags.dat";
        String us = "data/user_ratedmovies.dat";
        
        // Row, COl, Colue, Ignore lines
        ContentsRocchio instance = new ContentsRocchio(
                it,us, 0,1,2,1,0,1,2,1,1000
        );
        
        HashMap<Integer, String> pelis = null;
        try {
            pelis = instance.CargarColumnasFichero("data/movies.dat", 0, 1);
        } catch (IOException ex) {
            Logger.getLogger(ContentsRocchio.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("El fichero movies.dat no existe para leer los nombres de las películas.");
        }

        
        
        while( interactWithUser(instance,pelis) != 0);
            
        
    }

    private static int interactWithUser(ContentsRocchio instance,HashMap<Integer,String> pelis) {

        System.out.println("Introduzca un usuario al que recomendar (0 para salir):");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int user = 75;
        try {
            user = Integer.parseInt(br.readLine());
        } catch (IOException ex) {
            Logger.getLogger(ContentsRocchio.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No he reconocido ese valor, asique tomaré " + user);
        }
        if (user == 0)
            return 0;
        // Cogemos el correspondiente índice de la matriz para utilizar como usuario.
        if (instance.IdtoIdx_user.containsKey(user))
            user = instance.IdtoIdx_user.get(user);
        
        System.out.println("Índice en la matriz: " + user);
        //double result =0.0;

        System.out.println("Introduzca el número de items para recomendar:");
        br = new BufferedReader(new InputStreamReader(System.in));
        int size = 5;
        try {
            size = Integer.parseInt(br.readLine());
        } catch (IOException ex) {
            Logger.getLogger(ContentsRocchio.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No he reconocido ese valor, asique tomaré " + size);
        }

        instance.writeRatedItems(user, pelis);

        List<UserValue> l = instance.recommend(user, size);
        System.out.println("\nEste es el raking de Rocchio:\n");
        for (UserValue UV : l){
            System.out.println(pelis.get(instance.IdtoIdx_items.get(UV.user)) + "\t -- \t" + String.format("%.2f",UV.simil));
        }
            
        
        l.clear();
        return 1;
        //assertEquals(expResult, result, 0.01);
    }

    protected Matrix tagsItemsMatrix;
    protected Matrix centroides;

    protected int numTags;

    /**
     * Dado que los id's en el fichero pueden no estar ordenados de 1 a n (como
     * es el caso de movies.dat que empiezan en 75), necesitamos almacenar que
     * el id 75 se corresponde con la primera columna de la matriz. Para esto
     * están estos LinkedHashMap.
     */
    LinkedHashMap<Integer, Integer> IdtoIdx_user;
    LinkedHashMap<Integer, Integer> IdtoIdx_items;

    public void setIdtoIdxUser(LinkedHashMap<Integer, Integer> ididxuser) {
        IdtoIdx_user = ididxuser;
    }

    public void setIdtoIdxItem(LinkedHashMap<Integer, Integer> ididxitem) {
        IdtoIdx_items = ididxitem;
    }

    public ContentsRocchio(String fileofContents, int codeContents, String fileOfUsers, int codeUsers) {
        // Obtenemos las variables previas necesarias.
        numUser = Utils.getSizeOfFile(fileOfUsers);
        numItem = Utils.getSizeOfFile(fileofContents);
        // TODO: este 4 está puesto a pelo para satisfacer el ejemplo. 
        //	Hay que definir un Utils.getColumnsOfFile() para que funcione en todos los casos.
        numTags = 4;

        // Inicialización de variables utilizadas en los métodos load.
        IdtoIdx_items = new LinkedHashMap<>();
        IdtoIdx_user = new LinkedHashMap<>();
        // Cargamos matriz de items.
        loadContentsPorLineas(fileofContents, codeContents);

        // Cargamos matriz de usuarios.
        loadUserMatrixPorLineas(fileOfUsers, codeUsers);
        numUser = IdtoIdx_user.size();
        numItem = IdtoIdx_items.size();
        _calculateCentroides();
    }

    /**
     * Constructor
     *
     * @param fileofContents	Fichero con la información de los contenidos:
     * @param fileOfUsers	Fichero con la información de los usuarios y sus
     * votos.
     * @param userRowsIdx	Columna del fichero de usuarios en la que se
     * encuentran los ids de usuarios que serán las filas de la matriz.
     * @param userColsIdx	Columna del fichero de usuarios en la que se
     * encuentran los ids de los items vistos que serán las columnas de la
     * matriz.
     * @param userRankIdx	Columna del fichero de usuarios en la que se
     * encuentran las valoraciones de cada usuario
     * @param userIgnoreLines	Número de líneas del principio que ignorar del
     * fichero de usuarios (Típicamente 1 para ignorar cabeceras)
     * @param itemRowsIdx	Columna del fichero de items en la que se encuentran
     * los ids de los items que serán las filas de la matriz.
     * @param itemColsIdx	Columna del fichero de items en la que se encuentran
     * los ids de los items vistos que serán las columnas de la matriz.
     * @param itemRankIdx	Columna del fichero de items en la que se encuentran
     * las valoraciones de cada tags del item.
     * @param itemIgnoreLines	Número de líneas del principio que ignorar del
     * fichero de usuarios (Típicamente 1 para ignorar cabeceras).
     * @param step	Número de filas y columnas que crear dinámicamente cuando la
     * matriz se llene.
     */
    public ContentsRocchio(String fileofContents, String fileOfUsers,
            int userRowsIdx, int userColsIdx, int userRankIdx, int userIgnoreLines, int itemRowsIdx, int itemColsIdx, int itemRankIdx, int itemIgnoreLines,
            int step) {
        
            // Obtenemos las variables previas necesarias.
            
            // TODO: este 4 está puesto a pelo para satisfacer el ejemplo.
            //	Hay que definir un Utils.getColumnsOfFile() para que funcione en todos los casos.
            numTags = 4;
            
            // Inicialización de variables utilizadas en los métodos load.
            IdtoIdx_items = new LinkedHashMap<>();
            IdtoIdx_user = new LinkedHashMap<>();
            System.out.println("Cargando Items...");
            // Cargamos matriz de items.
            loadContents(fileofContents, itemRowsIdx, itemColsIdx, itemRankIdx, itemIgnoreLines, step, step);
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            
            System.out.println("Cargando Usuarios...");
            // Cargamos matriz de usuarios.
            loadUserMatrix(fileOfUsers, userRowsIdx, userColsIdx, userRankIdx, userIgnoreLines, step, IdtoIdx_items.size());
            
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            
            // Reajustamos dimensiones para poder multiplcar. Hay películas votadas que no tienen tags¿?
            int cols = matriz.getNumCols() - tagsItemsMatrix.getNumCols();
            int rows = matriz.getNumRows() - tagsItemsMatrix.getNumRows();
            /*System.out.println("cols:\n\tus: "+matriz.getNumCols()+"\t it: "+ tagsItemsMatrix.getNumCols()+ " = "+ cols);
            System.out.println("rows:\n\tus: " + matriz.getNumRows() + "\t it " + tagsItemsMatrix.getNumRows() + " = " + rows);
            System.out.println(matriz.getNumCols()*tagsItemsMatrix.getNumRows() < (Integer.MAX_VALUE/2));*/
            if (matriz.getNumCols() - tagsItemsMatrix.getNumCols() > 0) {
                tagsItemsMatrix.addColLast(matriz.getNumCols() - tagsItemsMatrix.getNumCols());
            }
            if (matriz.getNumCols() - tagsItemsMatrix.getNumCols() < 0) {
                matriz.addColLast(tagsItemsMatrix.getNumCols() - matriz.getNumCols());
            }
            
            if (matriz.getNumRows() - tagsItemsMatrix.getNumRows() > 0) {
                tagsItemsMatrix.addRowLast(matriz.getNumRows() - tagsItemsMatrix.getNumRows());
            }
            if (matriz.getNumRows() - tagsItemsMatrix.getNumRows() < 0) {
                matriz.addColLast(matriz.getNumCols() - tagsItemsMatrix.getNumCols());
            }
            
            System.out.println("Calculando centroides");
            
            _calculateCentroides();
            
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            
            
            
            
            
       
    }

    private void _calculateCentroides() {
        centroides = Matrix.producto(tagsItemsMatrix, matriz.transpose());
        centroides = centroides.RocchiNormalize(matriz.transpose());

    }

    /**
     * LOADS:
     *
     * Ambos métodos son muy parecidos.
     *
     * La matriz a rellenar es una variable de la clase y cada método rellena la
     * correspondiente. Lo mismo con el diccionario que mantiene la
     * correspondencia entre id's reales e índices de matrices. El CODE de cada
     * método corresponde al FilterCallable con el que se debe procesar cada
     * fichero. El último arg. indica si cada fila del fichero corresponde a una
     * columna de la matriz o no.
     */
    /**
     * Carga el contenido del fichero en las variables de la clase
     * correspondientes.
     *
     * @param fileOfContents	fichero a leer.
     * @param code	Código correspondiente al filtercallable asociado al fichero.
     */
    public final void loadContentsPorLineas(String fileOfContents, int code) {
        tagsItemsMatrix = new Matrix(numTags, numItem);
        super.CargarMatrizPorLineas(fileOfContents, tagsItemsMatrix, code, IdtoIdx_items, true);
    }

    /**
     * Carga el contenido del fichero en las variables de la clase
     * correspondientes.
     *
     * @param fileOfUsers	fichero a leer.
     * @param code	Código correspondiente al filtercallable asociado al fichero.
     */
    public final void loadUserMatrixPorLineas(String fileOfUsers, int code) {

        System.out.println(numUser + "*" + numItem + "=" + numUser * numItem);
        matriz = new Matrix(numUser, numItem);

        super.CargarMatrizPorLineas(fileOfUsers, matriz, code, IdtoIdx_user, false);

    }

    /**
     * Ranking de Rochio. Utiliza la función de similitud coseno.
     *
     * @param user
     * @param item
     * @return
     */
    @Override
    public double rank(int user, int item) {
        int idxuser = user;
        int idxitem = IdtoIdx_items.get(item);
        if (0 != matriz.getVal(idxuser,idxitem))
            return -2;
        double[] v1 = centroides.getCol(idxuser);
        double[] v2 = tagsItemsMatrix.getCol(idxitem);
        
        return Similitudes.coseno(v1, v2);
    }

    private void loadContents(String fileofContents, int rowsIdx, int colsIdx, int rankIdx, int ignoreLines, int rowstep, int colstep) {
        tagsItemsMatrix = super.cargarMatriz(fileofContents, rowsIdx, colsIdx, rankIdx, null, IdtoIdx_items, rowstep, colstep, ignoreLines);
    }

    private void loadUserMatrix(String fileOfUsers, int rowsIdx, int colsIdx, int rankIdx, int ignoreLines, int rowstep, int colstep) {
        matriz = super.cargarMatriz(fileOfUsers, rowsIdx, colsIdx, rankIdx, IdtoIdx_user, IdtoIdx_items, rowstep, colstep, ignoreLines);
    }
    
    @Override
    public List<UserValue> recommend(int user, int size) {
        ArrayList<UserValue> toret = new ArrayList<>();
        BinaryHeap heap = new BinaryHeap();

        double r = Double.MIN_VALUE;

        
        for (Integer it : this.IdtoIdx_items.keySet()) {
            r = this.rank(user, it);
            // Ignoramos los que han sido puntuados.
            if (r == -2)
                continue;
            if (heap.size() < size) {
                heap.insert(new UserValue(it, r));
            } else if (((UserValue) heap.findMin()).simil < r) {
                heap.deleteMin();
                heap.insert(new UserValue(it,r));
            }
        }
        while (!heap.isEmpty()) {
            toret.add((UserValue) heap.deleteMin());
        }

        return toret;
    }

    @Override
    public void writeRatedItems(int user, HashMap<Integer, String> pelis) {
        int movieid = 0;
        String titulo = "";
        double[] valoresmios = this.matriz.getRow(user);
        System.out.println("Los items puntuados por el usuario son los siguientes:");
        for (int x = 0; x < valoresmios.length; x++) {
            if (valoresmios[x] != 0) {

                for (Integer key : IdtoIdx_items.keySet()) {
                    if (IdtoIdx_items.get(key) == x) {
                        movieid = key;
                        break;
                    }
                }
                titulo = pelis == null ? "" + movieid : pelis.get(movieid);
                System.out.println("Pelicula \"" + titulo + "\" (" + movieid + "): Con una puntuacion de " + valoresmios[x]);
            }
        }
    }

}
