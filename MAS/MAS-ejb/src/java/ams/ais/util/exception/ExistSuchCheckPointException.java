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
public class ExistSuchCheckPointException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchCheckPointException</code>
     * without detail message.
     */
    public ExistSuchCheckPointException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchCheckPointException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchCheckPointException(String msg) {
        super(msg);
    }
}
