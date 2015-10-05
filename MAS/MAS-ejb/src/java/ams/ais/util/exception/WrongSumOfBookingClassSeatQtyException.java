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
public class WrongSumOfBookingClassSeatQtyException extends Exception {

    /**
     * Creates a new instance of
     * <code>WrongSumOfBookingClassSeatQtyException</code> without detail
     * message.
     */
    public WrongSumOfBookingClassSeatQtyException() {
    }

    /**
     * Constructs an instance of
     * <code>WrongSumOfBookingClassSeatQtyException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public WrongSumOfBookingClassSeatQtyException(String msg) {
        super(msg);
    }
}
