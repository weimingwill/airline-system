/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.util.exception;

/**
 *
 * @author ChuningLiu
 */
public class NoSuchAirTicketException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchAirTicketException</code> without
     * detail message.
     */
    public NoSuchAirTicketException() {
    }

    /**
     * Constructs an instance of <code>NoSuchAirTicketException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchAirTicketException(String msg) {
        super(msg);
    }
}
