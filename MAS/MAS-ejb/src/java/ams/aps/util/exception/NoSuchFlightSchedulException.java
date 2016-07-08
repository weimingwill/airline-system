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
public class NoSuchFlightSchedulException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchFlightSchedulException</code>
     * without detail message.
     */
    public NoSuchFlightSchedulException() {
    }

    /**
     * Constructs an instance of <code>NoSuchFlightSchedulException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchFlightSchedulException(String msg) {
        super(msg);
    }
}
