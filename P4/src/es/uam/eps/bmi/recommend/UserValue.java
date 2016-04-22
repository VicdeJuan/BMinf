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
public class UserValue implements Comparable{
    int user;
    double simil;

    public UserValue(int user, double simil) {
        this.user = user;
        this.simil = simil;
    }

    public int getUser() {
        return user;
    }

    public double getSimil() {
        return simil;
    }

    public void setSimil(double simil) {
        this.simil = simil;
    }

    @Override
    //si this <= a comparar =>1
    public int compareTo(Object o) {
        UserValue otro= (UserValue) o;
        if(this.simil<= otro.getSimil()){
            return 1;
        }
        else{ return -1;}
    }
    
}
