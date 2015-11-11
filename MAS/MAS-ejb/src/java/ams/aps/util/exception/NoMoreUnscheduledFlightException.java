/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.exception;

/**
 *
 * @author winga_000
 */
public class NoMoreUnscheduledFlightException extends Exception {

    /**
     * Creates a new instance of <code>NoMoreUnscheduledFlightException</code>
     * without detail message.
     */
    public NoMoreUnscheduledFlightException() {
    }

    /**
     * Constructs an instance of <code>NoMoreUnscheduledFlightException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoMoreUnscheduledFlightException(String msg) {
        super(msg);
    }
}
