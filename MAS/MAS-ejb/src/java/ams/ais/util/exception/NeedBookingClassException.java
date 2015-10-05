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
public class NeedBookingClassException extends Exception {

    /**
     * Creates a new instance of <code>NeedBookingClassException</code> without
     * detail message.
     */
    public NeedBookingClassException() {
    }

    /**
     * Constructs an instance of <code>NeedBookingClassException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NeedBookingClassException(String msg) {
        super(msg);
    }
}
