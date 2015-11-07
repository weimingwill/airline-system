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
public class DeleteFailedException extends Exception {

    /**
     * Creates a new instance of <code>DeleteFailedException</code> without
     * detail message.
     */
    public DeleteFailedException() {
    }

    /**
     * Constructs an instance of <code>DeleteFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteFailedException(String msg) {
        super(msg);
    }
}
