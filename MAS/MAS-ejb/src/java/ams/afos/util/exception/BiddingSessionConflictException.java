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
public class BiddingSessionConflictException extends Exception {

    /**
     * Creates a new instance of <code>BiddingSessionConflictException</code>
     * without detail message.
     */
    public BiddingSessionConflictException() {
    }

    /**
     * Constructs an instance of <code>BiddingSessionConflictException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BiddingSessionConflictException(String msg) {
        super(msg);
    }
}
