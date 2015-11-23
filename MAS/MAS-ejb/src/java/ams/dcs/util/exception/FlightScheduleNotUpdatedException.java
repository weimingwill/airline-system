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
public class FlightScheduleNotUpdatedException extends Exception {

    /**
     * Creates a new instance of <code>FlightScheduleNotUpdatedException</code>
     * without detail message.
     */
    public FlightScheduleNotUpdatedException() {
    }

    /**
     * Constructs an instance of <code>FlightScheduleNotUpdatedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FlightScheduleNotUpdatedException(String msg) {
        super(msg);
    }
}
