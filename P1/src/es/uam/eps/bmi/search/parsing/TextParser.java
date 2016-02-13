/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.search.parsing;

/**
 *
 * @author e267044
 */
public interface TextParser {

    /** devolverá procesado un texto de entrada.
     *
     * @param text
     * @return
     */
    String parse(String text);

    
}
