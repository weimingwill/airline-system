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
public class NoSuchUsernameException extends Exception {

    /**
     * Creates a new instance of <code>UserDoesNotExistException</code> without
     * detail message.
     */
    public NoSuchUsernameException() {
    }

    /**
     * Constructs an instance of <code>UserDoesNotExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchUsernameException(String msg) {
        super(msg);
    }
}
