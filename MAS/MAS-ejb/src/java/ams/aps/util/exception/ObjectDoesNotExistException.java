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
public class ObjectDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>ObjectDoesNotExistException</code>
     * without detail message.
     */
    public ObjectDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>ObjectDoesNotExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ObjectDoesNotExistException(String msg) {
        super(msg);
    }
}
