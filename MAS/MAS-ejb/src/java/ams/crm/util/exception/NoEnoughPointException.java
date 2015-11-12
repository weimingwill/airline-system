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
public class NoEnoughPointException extends Exception {

    /**
     * Creates a new instance of <code>NoEnoughPointException</code> without
     * detail message.
     */
    public NoEnoughPointException() {
    }

    /**
     * Constructs an instance of <code>NoEnoughPointException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoEnoughPointException(String msg) {
        super(msg);
    }
}
