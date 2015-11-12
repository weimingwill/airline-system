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
public class NoSuchRegCustException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchRegCustException</code> without
     * detail message.
     */
    public NoSuchRegCustException() {
    }

    /**
     * Constructs an instance of <code>NoSuchRegCustException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchRegCustException(String msg) {
        super(msg);
    }
}
