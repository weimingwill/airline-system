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
public class NoSuchCabinClassTicketFamilyException extends Exception {

    /**
     * Creates a new instance of
     * <code>NoSuchCabinClassTicketFamilyException</code> without detail
     * message.
     */
    public NoSuchCabinClassTicketFamilyException() {
    }

    /**
     * Constructs an instance of
     * <code>NoSuchCabinClassTicketFamilyException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchCabinClassTicketFamilyException(String msg) {
        super(msg);
    }
}
