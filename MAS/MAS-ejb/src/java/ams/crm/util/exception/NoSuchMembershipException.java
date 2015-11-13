/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.exception;

/**
 *
 * @author Bowen
 */
public class NoSuchMembershipException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchMembershipException</code> without
     * detail message.
     */
    public NoSuchMembershipException() {
    }

    /**
     * Constructs an instance of <code>NoSuchMembershipException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchMembershipException(String msg) {
        super(msg);
    }
}
