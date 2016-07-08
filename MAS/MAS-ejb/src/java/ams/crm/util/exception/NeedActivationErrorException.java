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
public class NeedActivationErrorException extends Exception {

    /**
     * Creates a new instance of <code>NeedActivationErrorException</code>
     * without detail message.
     */
    public NeedActivationErrorException() {
    }

    /**
     * Constructs an instance of <code>NeedActivationErrorException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NeedActivationErrorException(String msg) {
        super(msg);
    }
}
