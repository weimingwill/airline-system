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
public class NoSuchPhaseDemandException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchPhaseDemandException</code> without
     * detail message.
     */
    public NoSuchPhaseDemandException() {
    }

    /**
     * Constructs an instance of <code>NoSuchPhaseDemandException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchPhaseDemandException(String msg) {
        super(msg);
    }
}
