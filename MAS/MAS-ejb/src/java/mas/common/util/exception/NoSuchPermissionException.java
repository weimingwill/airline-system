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
public class NoSuchPermissionException extends Exception {

    /**
     * Creates a new instance of <code>PermissionDoesNotExistException</code>
     * without detail message.
     */
    public NoSuchPermissionException() {
    }

    /**
     * Constructs an instance of <code>PermissionDoesNotExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchPermissionException(String msg) {
        super(msg);
    }
}
