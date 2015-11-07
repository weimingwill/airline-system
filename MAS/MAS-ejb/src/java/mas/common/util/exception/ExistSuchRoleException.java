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
public class ExistSuchRoleException extends Exception {

    /**
     * Creates a new instance of <code>RoleExistException</code> without detail
     * message.
     */
    public ExistSuchRoleException() {
    }

    /**
     * Constructs an instance of <code>RoleExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchRoleException(String msg) {
        super(msg);
    }
}
