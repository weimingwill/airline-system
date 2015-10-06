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
public class NeedResetDigestException extends Exception {

    /**
     * Creates a new instance of <code>NeedResetDigestException</code> without
     * detail message.
     */
    public NeedResetDigestException() {
    }

    /**
     * Constructs an instance of <code>NeedResetDigestException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NeedResetDigestException(String msg) {
        super(msg);
    }
}
