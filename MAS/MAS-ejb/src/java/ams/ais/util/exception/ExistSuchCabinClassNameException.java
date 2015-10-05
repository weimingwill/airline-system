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
public class ExistSuchCabinClassNameException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchCabinClassException</code>
     * without detail message.
     */
    public ExistSuchCabinClassNameException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchCabinClassException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchCabinClassNameException(String msg) {
        super(msg);
    }
}
