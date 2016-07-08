/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.util.exception;

/**
 *
 * @author Lewis
 */
public class FlightDutyConflictException extends Exception {

    /**
     * Creates a new instance of <code>FlightDutyExistsException</code> without
     * detail message.
     */
    public FlightDutyConflictException() {
    }

    /**
     * Constructs an instance of <code>FlightDutyExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FlightDutyConflictException(String msg) {
        super(msg);
    }
}
