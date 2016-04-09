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
public class ItemValue implements Comparable{
    int item;
    double value;

    public ItemValue(int item, double value) {
        this.item = item;
        this.value = value;
    }

    public int getItem() {
        return item;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        ItemValue aux= (ItemValue) o;
        double resta=-this.getValue()+aux.getValue();
        
       return ((int) resta);
               
       
    }
    
}
