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
public class ExistSuchTicketFamilyException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchTicketFamilyException</code>
     * without detail message.
     */
    public ExistSuchTicketFamilyException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchTicketFamilyException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchTicketFamilyException(String msg) {
        super(msg);
    }
}
