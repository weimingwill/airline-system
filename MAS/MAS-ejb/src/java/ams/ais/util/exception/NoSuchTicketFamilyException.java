/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author Bowen
 */
public class NoSuchTicketFamilyException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchTicketFamilyException</code>
     * without detail message.
     */
    public NoSuchTicketFamilyException() {
    }

    /**
     * Constructs an instance of <code>NoSuchTicketFamilyException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchTicketFamilyException(String msg) {
        super(msg);
    }
}
