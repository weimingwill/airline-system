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
public class EmailDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>EmailDoesNotExistException</code> without
     * detail message.
     */
    public EmailDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>EmailDoesNotExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmailDoesNotExistException(String msg) {
        super(msg);
    }
}
