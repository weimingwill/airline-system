/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.util.exception;

/**
 *
 * @author ChuningLiu
 */
public class NoSuchPNRException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchPNRException</code> without detail
     * message.
     */
    public NoSuchPNRException() {
    }

    /**
     * Constructs an instance of <code>NoSuchPNRException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchPNRException(String msg) {
        super(msg);
    }
}
