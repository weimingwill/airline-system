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
public class ExistSuchTicketFamilyTypeException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchTicketFamilyTypeException</code>
     * without detail message.
     */
    public ExistSuchTicketFamilyTypeException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchTicketFamilyTypeException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchTicketFamilyTypeException(String msg) {
        super(msg);
    }
}
