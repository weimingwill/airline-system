/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author winga_000
 */
public class NoSuchBookingClassHelperException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchBookingClassHelperException</code>
     * without detail message.
     */
    public NoSuchBookingClassHelperException() {
    }

    /**
     * Constructs an instance of <code>NoSuchBookingClassHelperException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchBookingClassHelperException(String msg) {
        super(msg);
    }
}
