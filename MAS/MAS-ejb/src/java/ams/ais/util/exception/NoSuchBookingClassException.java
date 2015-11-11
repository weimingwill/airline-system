/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author Tongtong
 */
public class NoSuchBookingClassException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchBookingClassException</code>
     * without detail message.
     */
    public NoSuchBookingClassException() {
    }

    /**
     * Constructs an instance of <code>NoSuchBookingClassException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchBookingClassException(String msg) {
        super(msg);
    }
}
