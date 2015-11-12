/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.util.exception;

/**
 *
 * @author Lewis
 */
public class PairingConflictException extends Exception {

    /**
     * Creates a new instance of <code>PairingConflictException</code> without
     * detail message.
     */
    public PairingConflictException() {
    }

    /**
     * Constructs an instance of <code>PairingConflictException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PairingConflictException(String msg) {
        super(msg);
    }
}
