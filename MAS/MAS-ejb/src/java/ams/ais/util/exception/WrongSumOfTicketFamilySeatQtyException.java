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
public class WrongSumOfTicketFamilySeatQtyException extends Exception {

    /**
     * Creates a new instance of
     * <code>WrongSumOfTicketFamilySeatQtyException</code> without detail
     * message.
     */
    public WrongSumOfTicketFamilySeatQtyException() {
    }

    /**
     * Constructs an instance of
     * <code>WrongSumOfTicketFamilySeatQtyException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public WrongSumOfTicketFamilySeatQtyException(String msg) {
        super(msg);
    }
}
