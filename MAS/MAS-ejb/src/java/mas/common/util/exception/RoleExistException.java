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
public class RoleExistException extends Exception {

    /**
     * Creates a new instance of <code>RoleExistException</code> without detail
     * message.
     */
    public RoleExistException() {
    }

    /**
     * Constructs an instance of <code>RoleExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoleExistException(String msg) {
        super(msg);
    }
}
