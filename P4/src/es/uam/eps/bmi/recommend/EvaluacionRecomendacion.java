/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import javafx.util.Pair;

/**
 *
 * @author dani
 */
public class EvaluacionRecomendacion {

    double train;
    double test;

    public static void main(String[] argv) throws IOException {
        EvaluacionRecomendacion eva = new EvaluacionRecomendacion(0.2, 0.8);

        String ruta = "EvaluacionKNNColaborativo.csv";
        File archivo = new File(ruta);
        BufferedWriter bw;

        if (archivo.exists()) {
            bw = new BufferedWriter(new FileWriter(archivo));

        } else {
            bw = new BufferedWriter(new FileWriter(archivo));
        }

        bw.write("Vecinos , MAE, MRSE\n");

        int vecinos = 25;
        double MAE = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE + " para " + vecinos + " vecinos");
        double RMSE = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE + "," + RMSE + "\n");

        vecinos = 50;
        double MAE1 = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE1 + " para " + vecinos + " vecinos");
        double RMSE1 = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE1 + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE1 + "," + RMSE1 + "\n");

        vecinos = 75;
        double MAE2 = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE2 + " para " + vecinos + " vecinos");
        double RMSE2 = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE2 + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE2 + "," + RMSE2 + "\n");

        vecinos = 100;
        double MAE3 = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE3 + " para " + vecinos + " vecinos");
        double RMSE3 = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE3 + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE3 + "," + RMSE3 + "\n");

        vecinos = 250;
        double MAE4 = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE4 + " para " + vecinos + " vecinos");
        double RMSE4 = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE4 + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE4 + "," + RMSE4 + "\n");

        vecinos = 500;
        double MAE5 = MAE(eva.getTest(), vecinos);
        System.out.println("El MAE es: " + MAE5 + " para " + vecinos + " vecinos");
        double RMSE5 = RMSE(eva.getTest(), vecinos);
        System.out.println("El RMSE es: " + RMSE5 + " para " + vecinos + " vecinos");
        bw.write(vecinos + "," + MAE5 + "," + RMSE5 + "\n");

        bw.close();

    }

    public EvaluacionRecomendacion(double train, double test) {
        this.train = 0.8;
        this.test = 0.2;
    }

    public double getTrain() {
        return train;
    }

    public double getTest() {
        return test;
    }

    public static double MAE(double test, int vecinos) {

        double dividendo = 0.0;
        double resta = 0.0;
        //ColaborativeFiltering instance = new ColaborativeFiltering(vecinos, "data/user_ratedmovies.dat");
        ColaborativeFiltering instance = new ColaborativeFiltering(vecinos, "data/user_ratedmovies.dat");

        double random = 0.0;
        Random r = new Random();
        double[] sinCeros = instance.matriz.getData().clone();
        int numCols = instance.matriz.getNumCols();
        int numRows = instance.matriz.getNumRows();
        Matrix real = new Matrix(sinCeros, numRows, numCols);
        List<Componente> tests = new ArrayList();
        //real.set(0, 0, 89);
        //Meto un 20% de ceros en la matriz para "entrenar"
        for (int k = 0; k < instance.matriz.getNumRows(); k++) {
            for (int j = 0; j < instance.matriz.getNumCols(); j++) {
                random = r.nextDouble();
                if (random <= test) {
                    if (instance.matriz.getRow(k)[j] != 0) {
                        instance.matriz.set(k, j, 0);
                        Componente comp = new Componente(k, j);
                        tests.add(comp);
                    }
                }

            }
        }

        //instance.rank(1, 19);
        for (Componente nuestro : tests) {
            int i = nuestro.getI();
            int j = nuestro.getJ();
            double prediccion = instance.rank(i, j);
            double ratingreal = real.getRow(i)[j];
            if (prediccion!=1.01){
                dividendo = dividendo + 1.0;
                //System.out.println("prediccion: "+prediccion+" rating real: "+ratingreal);
                resta = resta + Math.abs(prediccion - ratingreal);
            }
        }

        //System.out.println("divisor: "+dividendo+" operacion: "+resta);
        double MAE = (1 / dividendo) * resta;

        return MAE;
    }

    public static double RMSE(double test, int vecinos) {

        double dividendo = 0.0;
        double resta = 0.0;
        ColaborativeFiltering instance = new ColaborativeFiltering(vecinos, "data/user_ratedmovies.dat");
        double random = 0.0;
        Random r = new Random();
        double[] sinCeros = instance.matriz.getData().clone();
        int numCols = instance.matriz.getNumCols();
        int numRows = instance.matriz.getNumRows();
        List<Componente> tests = new ArrayList();
        Matrix real = new Matrix(sinCeros, numRows, numCols);

        //real.set(0, 0, 89);
        //Meto un 20% de ceros en la matriz para "entrenar"
        for (int k = 0; k < instance.matriz.getNumRows(); k++) {
            for (int j = 0; j < instance.matriz.getNumCols(); j++) {
                random = r.nextDouble();
                if (random <= test) {
                    if (instance.matriz.getRow(k)[j] != 0) {
                        instance.matriz.set(k, j, 0);
                        Componente comp = new Componente(k, j);
                        tests.add(comp);
                    }
                }

            }
        }
        for (Componente nuestro : tests) {
            int i = nuestro.getI();
            int j = nuestro.getJ();
            double prediccion = instance.rank(i, j);
            double ratingreal = real.getRow(i)[j];
            if (prediccion!=1.01){
            dividendo = dividendo + 1.0;
            //System.out.println("prediccion: "+prediccion+" rating real: "+ratingreal);
            resta += Math.pow(prediccion - ratingreal, 2);
            }
        }



        double RMSE = Math.sqrt((1 / dividendo) * resta);

        return RMSE;
    }

}
