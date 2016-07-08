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
public class NoSuchRouteException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchRouteException</code> without
     * detail message.
     */
    public NoSuchRouteException() {
    }

    /**
     * Constructs an instance of <code>NoSuchRouteException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchRouteException(String msg) {
        super(msg);
    }
}
