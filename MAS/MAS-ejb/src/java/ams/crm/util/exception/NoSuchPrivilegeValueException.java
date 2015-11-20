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
public class NoSuchPrivilegeValueException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchPrivilegeValue</code> without
     * detail message.
     */
    public NoSuchPrivilegeValueException() {
    }

    /**
     * Constructs an instance of <code>NoSuchPrivilegeValue</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchPrivilegeValueException(String msg) {
        super(msg);
    }
}
