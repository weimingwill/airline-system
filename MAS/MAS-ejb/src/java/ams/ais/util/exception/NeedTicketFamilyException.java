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
public class NeedTicketFamilyException extends Exception {

    /**
     * Creates a new instance of <code>NeedTicketFamilyException</code> without
     * detail message.
     */
    public NeedTicketFamilyException() {
    }

    /**
     * Constructs an instance of <code>NeedTicketFamilyException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NeedTicketFamilyException(String msg) {
        super(msg);
    }
}
