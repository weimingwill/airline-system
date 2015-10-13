/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.exception;

/**
 *
 * @author Lewis
 */
public class ObjectExistsException extends Exception {

    /**
     * Creates a new instance of <code>ObjectExistsException</code> without
     * detail message.
     */
    public ObjectExistsException() {
    }

    /**
     * Constructs an instance of <code>ObjectExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ObjectExistsException(String msg) {
        super(msg);
    }
}
