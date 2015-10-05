/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author Tongtong
 */
public class NoSuchFlightScheduleBookingClassException extends Exception {

    /**
     * Creates a new instance of
     * <code>NoSuchFlightScheduleBookingClassException</code> without detail
     * message.
     */
    public NoSuchFlightScheduleBookingClassException() {
    }

    /**
     * Constructs an instance of
     * <code>NoSuchFlightScheduleBookingClassException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchFlightScheduleBookingClassException(String msg) {
        super(msg);
    }
}
