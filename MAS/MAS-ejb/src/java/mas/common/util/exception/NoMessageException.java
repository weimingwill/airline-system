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
public class NoMessageException extends Exception {

    /**
     * Creates a new instance of <code>NoMessageException</code> without detail
     * message.
     */
    public NoMessageException() {
    }

    /**
     * Constructs an instance of <code>NoMessageException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoMessageException(String msg) {
        super(msg);
    }
}
