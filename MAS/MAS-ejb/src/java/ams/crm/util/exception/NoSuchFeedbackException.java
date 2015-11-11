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
public class NoSuchFeedbackException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchFeedbackException</code> without
     * detail message.
     */
    public NoSuchFeedbackException() {
    }

    /**
     * Constructs an instance of <code>NoSuchFeedbackException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchFeedbackException(String msg) {
        super(msg);
    }
}
