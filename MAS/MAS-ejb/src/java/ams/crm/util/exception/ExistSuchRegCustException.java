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
public class ExistSuchRegCustException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchRegCustException</code> without
     * detail message.
     */
    public ExistSuchRegCustException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchRegCustException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchRegCustException(String msg) {
        super(msg);
    }
}
