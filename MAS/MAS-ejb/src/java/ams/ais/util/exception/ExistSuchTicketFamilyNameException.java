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
public class ExistSuchTicketFamilyNameException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchTicketFamilyNameException</code>
     * without detail message.
     */
    public ExistSuchTicketFamilyNameException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchTicketFamilyNameException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchTicketFamilyNameException(String msg) {
        super(msg);
    }
}
