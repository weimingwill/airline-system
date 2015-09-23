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
public class EmptyTableException extends Exception {

    /**
     * Creates a new instance of <code>EmptyTableException</code> without detail
     * message.
     */
    public EmptyTableException() {
    }

    /**
     * Constructs an instance of <code>EmptyTableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EmptyTableException(String msg) {
        super(msg);
    }
}
