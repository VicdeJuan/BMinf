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
import java.io.PrintWriter;
import static java.lang.reflect.Array.set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public static void main(String[] argv) throws FileNotFoundException, IOException, Exception {
        /*
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
DenseMatrix64F B=null;
        try {

             B= MatrixIO.loadCSV("data/user_ratedmovies.csv");
             B.print();

            //B.print();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("No falla");

        int userid = 2;
    //ColaborativeFiltering instance = new ColaborativeFiltering(B,2);
      ColaborativeFiltering instance = new ColaborativeFiltering(2, "data/movie_tags_reducido.dat", "data/user_ratedmovies_reducido.dat");
        instance.Knn(userid);

    }

    public ColaborativeFiltering(DenseMatrix64F matris, int k) {
        this.k = k;
        matriz = new Matrix(matris);
        IdtoIdx_user = new LinkedHashMap<>();
    }

    public ColaborativeFiltering(int k, String fileofContents, String fileOfUsers) {
        this.k = k;
        // Obtenemos las variables previas necesarias.
        numUser = Utils.getSizeOfFile(fileOfUsers);
        numItem = Utils.getSizeOfFile(fileofContents);
		// TODO: este 4 está puesto a pelo para satisfacer el ejemplo. 
        //	Hay que definir un Utils.getColumnsOfFile() para que funcione en todos los casos.

        // Inicialización de variables utilizadas en los métodos load.
        IdtoIdx_user = new LinkedHashMap<>();
		// Cargamos matriz de items.

        // Cargamos matriz de usuarios.
        loadUserMatrix(fileOfUsers);
    }

    @Override
    public double rank(int user, int item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void Knn(int usuarioArecomendar) throws Exception {

        Heap heap = new Heap(k);

        for (Integer user : IdtoIdx_user.keySet()) {
            
            int filauser = IdtoIdx_user.get(user);
            int filamiuser = IdtoIdx_user.get(usuarioArecomendar);
            
            if(filauser==filamiuser) continue;
            double[] valoresuser = matriz.getRow(filauser);
            double[] valoresmios = matriz.getRow(filamiuser);
            double simil = Similitudes.coseno(valoresuser, valoresmios);
            //añadimos La similitud de lo usuarios al heap

            UserValue u = new UserValue(user, simil);
            if (heap.min().compareTo(u) == 1) {
                if (!heap.isFull()) {
                    heap.insert(u);
                } else {
                    heap.removeMin();
                    heap.insert(u);
                }

            }
        }
        List<UserValue> maxUsers = new ArrayList();
        for (Object maxuser : heap.getS()) {
            UserValue max = (UserValue) maxuser;
            maxUsers.add(max);
        }
        recommend(maxUsers,usuarioArecomendar);

    }

    public void recommend(List<UserValue> maxusers, int usuarioArecomendar) {
        for (int i = 0; i <maxusers.size(); i++) {
            int usuario=IdtoIdx_user.get(maxusers.get(i).getUser());
            
            //double[] valoresmios = matriz.getRow(0);
            
        }
    }

    public final void loadUserMatrix(String fileOfUsers) {

        matriz = new Matrix(numUser, numItem);

        //super.CargarMatriz(fileOfUsers, matriz, FilterCallableUserMovies.CODE, IdtoIdx_user, false);
    }

}
