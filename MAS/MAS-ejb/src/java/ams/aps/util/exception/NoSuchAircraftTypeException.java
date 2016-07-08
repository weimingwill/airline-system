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
public class NoSuchAircraftTypeException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchAircraftTypeException</code>
     * without detail message.
     */
    public NoSuchAircraftTypeException() {
    }

    /**
     * Constructs an instance of <code>NoSuchAircraftTypeException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchAircraftTypeException(String msg) {
        super(msg);
    }
}
