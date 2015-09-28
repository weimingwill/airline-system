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
public class NoSuchAircraftException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchAircraftException</code> without
     * detail message.
     */
    public NoSuchAircraftException() {
    }

    /**
     * Constructs an instance of <code>NoSuchAircraftException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchAircraftException(String msg) {
        super(msg);
    }
}
