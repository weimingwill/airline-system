/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.util.exception;

/**
 *
 * @author weiming
 */
public class ExistSuchFlightScheduleException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchFlightScheduleException</code>
     * without detail message.
     */
    public ExistSuchFlightScheduleException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchFlightScheduleException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchFlightScheduleException(String msg) {
        super(msg);
    }
}
