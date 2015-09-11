/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.exception;

/**
 *
 * @author winga_000
 */
public class PermissionExistException extends Exception {

    /**
     * Creates a new instance of <code>PermissionExistException</code> without
     * detail message.
     */
    public PermissionExistException() {
    }

    /**
     * Constructs an instance of <code>PermissionExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PermissionExistException(String msg) {
        super(msg);
    }
}
