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
public class BoardingErrorException extends Exception {

    /**
     * Creates a new instance of <code>BoardingErrorException</code> without
     * detail message.
     */
    public BoardingErrorException() {
    }

    /**
     * Constructs an instance of <code>BoardingErrorException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BoardingErrorException(String msg) {
        super(msg);
    }
}
