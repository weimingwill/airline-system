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
public class NoSuchCabinClassException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchCabinClassException</code> without
     * detail message.
     */
    public NoSuchCabinClassException() {
    }

    /**
     * Constructs an instance of <code>NoSuchCabinClassException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchCabinClassException(String msg) {
        super(msg);
    }
}
