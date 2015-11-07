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
public class ExistSuchBookingClassNameException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchBookingClassNameException</code>
     * without detail message.
     */
    public ExistSuchBookingClassNameException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchBookingClassNameException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchBookingClassNameException(String msg) {
        super(msg);
    }
}
