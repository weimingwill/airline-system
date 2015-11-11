/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.util.exception;

/**
 *
 * @author Bowen
 */
public class ExistSuchRuleException extends Exception {

    /**
     * Creates a new instance of <code>ExistSuchRuleException</code> without
     * detail message.
     */
    public ExistSuchRuleException() {
    }

    /**
     * Constructs an instance of <code>ExistSuchRuleException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExistSuchRuleException(String msg) {
        super(msg);
    }
}
