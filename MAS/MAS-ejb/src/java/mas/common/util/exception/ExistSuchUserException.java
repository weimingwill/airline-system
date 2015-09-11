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
public class ExistSuchUserException extends Exception {

    /**
     * Creates a new instance of <code>UserExistException</code> without detail
     * message.
     */
    public ExistSuchUserException() {
    }

    /**
     * Constructs an instance of <code>UserExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchUserException(String msg) {
        super(msg);
    }
}
