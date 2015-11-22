/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.util.helper;

/**
 *
 * @author Lewis
 */
public class NoCrewFoundException extends Exception {

    /**
     * Creates a new instance of <code>NoCrewFoundException</code> without
     * detail message.
     */
    public NoCrewFoundException() {
    }

    /**
     * Constructs an instance of <code>NoCrewFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoCrewFoundException(String msg) {
        super(msg);
    }
}
