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
public class NoSuchFlightException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchFlightException</code> without
     * detail message.
     */
    public NoSuchFlightException() {
    }

    /**
     * Constructs an instance of <code>NoSuchFlightException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchFlightException(String msg) {
        super(msg);
    }
}
