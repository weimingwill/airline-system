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
public class NoSelectAircraftException extends Exception {

    /**
     * Creates a new instance of <code>NoSelectAircraftException</code> without
     * detail message.
     */
    public NoSelectAircraftException() {
    }

    /**
     * Constructs an instance of <code>NoSelectAircraftException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSelectAircraftException(String msg) {
        super(msg);
    }
}
