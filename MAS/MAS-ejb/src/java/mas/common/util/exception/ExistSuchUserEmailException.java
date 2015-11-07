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
public class ExistSuchUserEmailException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchEmailException</code> without
     * detail message.
     */
    public ExistSuchUserEmailException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchEmailException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchUserEmailException(String msg) {
        super(msg);
    }
}
