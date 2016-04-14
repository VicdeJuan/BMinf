/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 *
 * @author dani
 */
public class EvaluacionRecomendacion {

    double train;
    double test;

    public static void main(String[] argv) {
        EvaluacionRecomendacion eva = new EvaluacionRecomendacion(0.8, 0.2);
        int vecinos =100;
        double MAE = MAE(eva.getTest(),vecinos);
        System.out.println("El MAE es: " + MAE);
        double RMSE = RMSE(eva.getTest(),vecinos);
        System.out.println("El RMSE es: " + RMSE);
        

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

        int dividendo = 0;
        double resta = 0.0;
        ColaborativeFiltering instance = new ColaborativeFiltering(vecinos, "data/user_ratedmovies.dat");
        double random = 0.0;
        Random r = new Random();
        double[] sinCeros = instance.matriz.getData().clone();
        int numCols = instance.matriz.getNumCols();
        int numRows = instance.matriz.getNumRows();
        Matrix real = new Matrix(sinCeros, numRows, numCols);

        //real.set(0, 0, 89);
        //Meto un 20% de ceros en la matriz para "entrenar"
        for (int k = 0; k < instance.matriz.getNumRows(); k++) {
            for (int j = 0; j < instance.matriz.getNumCols(); j++) {
                random = r.nextDouble();
                if (random <= test) {
                    instance.matriz.set(k, j, 0);
                }

            }
        }

        //instance.rank(1, 19);

        for (int k = 0; k < instance.matriz.getNumRows(); k++) {

            random = r.nextDouble();

            if (random <= test) {
                double[] row = instance.matriz.getRow(k);
                for (int j = 0; j < row.length; j++) {

                    double ratingreal = real.getRow(k)[j];
                    if (ratingreal != 0) {
                        random = r.nextDouble();
                        if (random <= test) {

                            double prediccion = instance.rank(k, j);
                            dividendo++;
                            //System.out.println("prediccion: "+prediccion+" rating real: "+ratingreal);
                            resta += Math.abs(prediccion - ratingreal);
                        }
                        //double ratingreal = instance.matriz.getRow(k)[j];

                    }
                }
            }
        }
        double MAE = (1 / dividendo) * resta;

        return MAE;
    }

    
    public static double RMSE(double test, int vecinos) {

        int dividendo = 0;
        double resta = 0.0;
        ColaborativeFiltering instance = new ColaborativeFiltering(vecinos, "data/user_ratedmovies.dat");
        double random = 0.0;
        Random r = new Random();
        double[] sinCeros = instance.matriz.getData().clone();
        int numCols = instance.matriz.getNumCols();
        int numRows = instance.matriz.getNumRows();
        Matrix real = new Matrix(sinCeros, numRows, numCols);

        //real.set(0, 0, 89);
        //Meto un 20% de ceros en la matriz para "entrenar"
        for (int k = 0; k < instance.matriz.getNumRows(); k++) {
            for (int j = 0; j < instance.matriz.getNumCols(); j++) {
                random = r.nextDouble();
                if (random <= test) {
                    instance.matriz.set(k, j, 0);
                }

            }
        }

        instance.rank(1, 19);

        for (int k = 0; k < instance.matriz.getNumRows(); k++) {

            random = r.nextDouble();

            if (random <= test) {
                double[] row = instance.matriz.getRow(k);
                for (int j = 0; j < row.length; j++) {

                    double ratingreal = real.getRow(k)[j];
                    if (ratingreal != 0) {
                        random = r.nextDouble();
                        if (random <= test) {

                            double prediccion = instance.rank(k, j);
                            dividendo++;
                            resta += Math.pow(prediccion - ratingreal, 2);
                        }
                        //double ratingreal = instance.matriz.getRow(k)[j];

                    }
                }
            }
        }
        double RMSE = Math.sqrt((1 / dividendo) * resta);

        return RMSE;
    }
    

}
