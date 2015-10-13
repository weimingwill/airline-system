/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author winga_000
 */
public class NoSuchTicketFamilyRuleException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchTicketFamilyRuleException</code>
     * without detail message.
     */
    public NoSuchTicketFamilyRuleException() {
    }

    /**
     * Constructs an instance of <code>NoSuchTicketFamilyRuleException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchTicketFamilyRuleException(String msg) {
        super(msg);
    }
}
