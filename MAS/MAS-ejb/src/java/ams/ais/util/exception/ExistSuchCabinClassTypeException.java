/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author Bowen
 */
public class ExistSuchCabinClassTypeException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchCabinClassTypeException</code>
     * without detail message.
     */
    public ExistSuchCabinClassTypeException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchCabinClassTypeException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchCabinClassTypeException(String msg) {
        super(msg);
    }
}
