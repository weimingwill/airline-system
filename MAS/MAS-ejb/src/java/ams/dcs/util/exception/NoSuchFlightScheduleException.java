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
public class NoSuchFlightScheduleException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchFlightScheduleException</code>
     * without detail message.
     */
    public NoSuchFlightScheduleException() {
    }

    /**
     * Constructs an instance of <code>NoSuchFlightScheduleException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchFlightScheduleException(String msg) {
        super(msg);
    }
}
