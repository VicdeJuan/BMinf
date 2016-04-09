/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

/**
 *
 * @author dani
 */
public class EvaluacionRecomendacion {
    double train;
    double test;

    public EvaluacionRecomendacion(double train, double test) {
        this.train = 0.8;
        this.test = 0.2;
    }
    
    public double MAE(){
    return 0.0;
    }
    
    public double RMSE(){
    return 0.0;
    }
    
}
