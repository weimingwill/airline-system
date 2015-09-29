/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.exception;

/**
 *
 * @author winga_000
 */
public class NoSuchAircraftCabinClassException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchAircraftCabinClassException</code>
     * without detail message.
     */
    public NoSuchAircraftCabinClassException() {
    }

    /**
     * Constructs an instance of <code>NoSuchAircraftCabinClassException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchAircraftCabinClassException(String msg) {
        super(msg);
    }
}
