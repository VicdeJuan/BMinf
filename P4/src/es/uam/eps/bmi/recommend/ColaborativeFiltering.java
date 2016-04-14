/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.reflect.Array.set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.MatrixIO;

/**
 *
 * @author dani
 */
public class ColaborativeFiltering extends RecommenderAbs {

    //número de vecinos
    int k;
    LinkedHashMap<Integer, Integer> IdtoIdx_user;
    LinkedHashMap<Integer, Integer> IdtoIdx_movies;

    public static void main(String[] argv) throws FileNotFoundException, IOException, Exception {

        String file = "data/user_ratedmovies_prueba.dat";

        ColaborativeFiltering instance;

        System.out.println("Introduzca un usuario para recomendar:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int user = Integer.parseInt(br.readLine());
        System.out.println("Usuario " + user);
        instance = new ColaborativeFiltering(10, file);

        instance.Knn(user);
        /*
        System.out.println(instance.rank(1355,5952));
        System.out.println(instance.rank(1355,107));
        
        
         String cadena;
         FileReader f = new FileReader("data/user_ratedmovies.dat");
         BufferedReader b = new BufferedReader(f);
         b.readLine();
         FileWriter fichero = null;
         PrintWriter pw = null;
         try
         {
         fichero = new FileWriter("data/user_ratedmovies.csv");
         pw = new PrintWriter(fichero);
         }
         catch (Exception e) {
         e.printStackTrace();
         }
         while((cadena = b.readLine())!=null) {
         String[] split = cadena.split("\t");
         pw.println(split[0]+" "+split[1]+" "+split[2]);        
           
         }
      
         b.close();
         fichero.close();
        
       
        
         DenseMatrix64F A = new DenseMatrix64F(2,3,true,new double[]{1,2,3,4,5,6});
 
         try {
         MatrixIO.saveCSV(A, "data/matrix_file.csv");
         DenseMatrix64F B = MatrixIO.loadCSV("matrix_file.csv");
         B.print();
         } catch (IOException e) {
         throw new RuntimeException(e);
         }
         */
        /*DenseMatrix64F B = null;
         try {

         B = MatrixIO.loadCSV("data/user_ratedmovies.csv");

         } catch (IOException e) {
         throw new RuntimeException(e);
         }*/
		//System.out.println("No falla");

		//int userid = 2;
        //instance = new ColaborativeFiltering(B, 2);
        //ColaborativeFiltering instance = new ColaborativeFiltering(2, "data/movie_tags_reducido.dat", "data/user_ratedmovies_reducido.dat");
		/*instance.Knn(userid);
         System.out.println(instance.rank(1, 0));*/
        //System.out.println(instance.rank(4, 1));
        //System.out.println(instance.rank(2, 2));
    }

    /**
     * Carga la variable local matriz con la información del fichero. Para más
     * información de los argumentos, consultar RecommenderAbs::CargarMatriz
     *
     * @param fileOfUsers
     */
    private void loadUserMatrix(String fileOfUsers) {
        int columnaDeUserID = 0;
        int columnaDeMovieID = 1;
        int columnaDeRating = 2;
        int numLineasCabecera = 1;
        int columnas = 150; // La matriz empezará con 10 columnas. Cada vez que se llene, se añadirán 10 más.
        int lineas = 200; //La matriz empezará con 200 filas. Cada vez que se llene, se añadirán 200 más.
        _loadUserMatrix(fileOfUsers, columnaDeUserID, columnaDeMovieID, columnaDeRating, numLineasCabecera, lineas, columnas);
    }

    private void _loadUserMatrix(String fileOfUsers, int rowsIdx, int colsIdx, int rankIdx, int ignoreLines, int rowstep, int colstep) {
        matriz = super.cargarMatriz(fileOfUsers, rowsIdx, colsIdx, rankIdx, IdtoIdx_user, IdtoIdx_movies, rowstep, colstep, ignoreLines);
        numUser = IdtoIdx_user.size();
    }

    /*
     public ColaborativeFiltering(DenseMatrix64F matris, int k) {
     this.k = k;
     matriz = new Matrix(matris);
     IdtoIdx_user = new LinkedHashMap<>();
     IdtoIdx_user.put(1, 0);
     IdtoIdx_user.put(2, 1);
     IdtoIdx_user.put(3, 2);
     IdtoIdx_user.put(4, 3);
     double d1[] = new double[3];
     double d2[] = new double[3];
     double d3[] = new double[3];
     double d4[] = new double[3];
     d1[0] = 4.0;
     d1[1] = 1.0;
     d1[2] = 2.0;
     d2[0] = 2.0;
     d2[1] = 0.0;
     d2[2] = 1.0;
     d3[0] = 0.0;
     d3[1] = 0.0;
     d3[2] = 1.0;
     d4[0] = 2.0;
     d4[1] = 1.0;
     d4[2] = 1.0;
     matriz.setRow(d1, 0);
     matriz.setRow(d2, 1);
     matriz.setRow(d3, 2);
     matriz.setRow(d4, 3);

     }
     */
    public ColaborativeFiltering(int k, String fileOfUsers) {
        this.k = k;
        // Obtenemos las variables previas necesarias.

        IdtoIdx_user = new LinkedHashMap<>();
        IdtoIdx_movies = new LinkedHashMap<>();

        //numItem = Utils.getSizeOfFile(fileofContents);
        // TODO: este 4 está puesto a pelo para satisfacer el ejemplo. 
        //	Hay que definir un Utils.getColumnsOfFile() para que funcione en todos los casos.
        // Inicialización de variables utilizadas en los métodos load.
        // Cargamos matriz de items.
        // Cargamos matriz de usuarios.
        loadUserMatrix(fileOfUsers);
    }

    public void Knn(int usuarioArecomendar) throws Exception {

        HashMap<Integer, String> todaspelis = CargarColumnasFichero("data/movies.dat", 0, 1);
        HashMap<Integer, String> pelis = new HashMap();
        int count = 0;
        for (Integer idpeli : this.IdtoIdx_movies.keySet()) {
            String nombrePeli = todaspelis.get(idpeli);
            Integer id = IdtoIdx_movies.get(idpeli);
            pelis.put(idpeli, nombrePeli);
            count++;
        }

        //meto el movieid de nuestra matriz y el nombre de la peli
        BinaryHeap heap = new BinaryHeap();

        if (!IdtoIdx_user.containsKey(usuarioArecomendar)) {
            System.out.println("No existe el usuario");
            return;
        }

        writeRatedItems(usuarioArecomendar, pelis);

        int filamiuser = IdtoIdx_user.getOrDefault(usuarioArecomendar, 0);
        double[] valoresmios = matriz.getRow(filamiuser);
        double[] valormovieid = matriz.getRow(filamiuser);
        for (Integer user : IdtoIdx_user.keySet()) {

            int filauser = IdtoIdx_user.get(user);

            if (filauser == filamiuser) {
                continue;
            }
            double[] valoresuser = matriz.getRow(filauser);

            double simil = Similitudes.coseno(valoresuser, valoresmios);
            simil = simil * -1;
            //añadimos La similitud de lo usuarios al heap
            if (Double.isNaN(simil)) {
                break;
            }
            UserValue u = new UserValue(user, simil);
            if (!heap.isEmpty()) {

                heap.insert(u);

            } else {
                heap.insert(u);
            }
        }
        List<UserValue> maxUsers = new ArrayList();

        for (int g = 0; g < this.k; g++) {
            UserValue maxuser = (UserValue) heap.deleteMin();
            maxuser.setSimil(maxuser.getSimil() * -1);
            UserValue max = (UserValue) maxuser;
            maxUsers.add(max);

        }
        int movieid = 0;

        recommend(maxUsers, usuarioArecomendar, pelis);

    }

    @Override
    public void writeRatedItems(int userId, HashMap<Integer, String> pelis) {
        int movieid = 0;
        String titulo = "";
        int user = this.IdtoIdx_user.get(userId);
        double[] valoresmios = this.matriz.getRow(user);
        System.out.println("Los items puntuados por el usuario son los siguientes:");
        for (int x = 0; x < valoresmios.length; x++) {
            if (valoresmios[x] != 0) {

                for (Integer key : IdtoIdx_movies.keySet()) {
                    if (IdtoIdx_movies.get(key) == x) {
                        movieid = key;
                        break;
                    }
                }
                titulo = pelis == null ? "" + movieid : pelis.get(movieid);
                System.out.println("Pelicula \"" + titulo + "\": Con una puntuacion de " + valoresmios[x]);
            }
        }
    }

    public void recommend(List<UserValue> userscercanos, int usuarioArecomendar, HashMap<Integer, String> pelis) {
        int filamiuser = IdtoIdx_user.getOrDefault(usuarioArecomendar, 0);
        //Solo voy a recomendar los items que el usuario no tenga un rating ya
        List<Integer> positemsvacios = new ArrayList();
        double[] valoresmios = matriz.getRow(filamiuser);

        for (int j = 0; j < valoresmios.length; j++) {
            if (valoresmios[j] == 0) {
                positemsvacios.add(j);
            }
        }
        double[] recomend = new double[valoresmios.length];
        Arrays.fill(recomend, 0.0);

        for (int i = 0; i < userscercanos.size(); i++) {

            int filausuario = IdtoIdx_user.get(userscercanos.get(i).getUser());
            double similitudUsuario = userscercanos.get(i).getSimil();

            double[] valoresuser = matriz.getRow(filausuario);
            for (int l = 0; l < positemsvacios.size(); l++) {
                int pos = positemsvacios.get(l);
                double rating = valoresuser[pos];
                recomend[pos] = recomend[pos] + similitudUsuario * rating;

            }

        }
        Set<ItemValue> itemRecomendacion;
        itemRecomendacion = new TreeSet();
        for (int j = 0; j < recomend.length; j++) {
            ItemValue aux = new ItemValue(j, recomend[j]);
            itemRecomendacion.add(aux);
        }

        System.out.println("Las recomendaciones para el usuario " + usuarioArecomendar + " Son las siguientes:");

        //cuantos queremos
        int max = 5;
        int cuenta = 0;
        for (ItemValue s : itemRecomendacion) {
            if (max == cuenta) {
                break;
            }
            cuenta++;
            if (pelis.get(s.getItem()) != null) {
                System.out.println("Pelicula " + pelis.get(s.getItem()) + " Con un ranking(que no rating, sin normalizar) de: " + s.getValue());
            }
        }
    }

    @Override
    public double rank(int user, int item) {

        BinaryHeap heap = new BinaryHeap();
        int filamiuser = user;
        double[] valoresmios = matriz.getRow(filamiuser);
        for (Integer use : IdtoIdx_user.keySet()) {

            int filauser = IdtoIdx_user.get(use);

            if (filauser == filamiuser) {
                continue;
            }
            double[] valoresuser = matriz.getRow(filauser);

            double simil = Similitudes.coseno(valoresuser, valoresmios);
            simil = simil * -1;
            //añadimos La similitud de lo usuarios al heap
            if (Double.isNaN(simil)) {
                break;
            }
            UserValue u = new UserValue(use, simil);
            if (!heap.isEmpty()) {

                heap.insert(u);

            } else {
                heap.insert(u);
            }
        }
        List<UserValue> maxUsers = new ArrayList();

        for (int g = 0; g < k; g++) {
            UserValue maxuser = (UserValue) heap.deleteMin();
            maxuser.setSimil(maxuser.getSimil() * -1);
            UserValue max = (UserValue) maxuser;
            maxUsers.add(max);

        }
        double[] ratingsItem = matriz.getCol(item);
        double prediccion = 0.0;
        double sumatorioSimilitudes = 0.0;
        //no va un 4 va matriz.getNumRows()
        for (int h = 0; h < maxUsers.size(); h++) {
            UserValue get = maxUsers.get(h);
            sumatorioSimilitudes += get.getSimil();
            prediccion += get.getSimil() * ratingsItem[h];

        }
        prediccion = prediccion * (1 / sumatorioSimilitudes);

        return prediccion;
    }

    @Override
    public List<UserValue> recommend(int user, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
