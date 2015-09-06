/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.exception;

/**
 *
 * @author Lewis
 */
public class RoleDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>RoleDoesNotExistException</code> without
     * detail message.
     */
    public RoleDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>RoleDoesNotExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoleDoesNotExistException(String msg) {
        super(msg);
    }
}
