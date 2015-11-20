/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.exception;

/**
 *
 * @author Bowen
 */
public class NoSuchBookingException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchBookingReferenceException</code>
     * without detail message.
     */
    public NoSuchBookingException() {
    }

    /**
     * Constructs an instance of <code>NoSuchBookingReferenceException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchBookingException(String msg) {
        super(msg);
    }
}
