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
public class NoUserExistException extends Exception {

    /**
     * Creates a new instance of <code>NoUserExistException</code> without
     * detail message.
     */
    public NoUserExistException() {
    }

    /**
     * Constructs an instance of <code>NoUserExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoUserExistException(String msg) {
        super(msg);
    }
}
