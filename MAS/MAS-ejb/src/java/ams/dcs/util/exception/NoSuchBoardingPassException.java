/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.util.exception;

/**
 *
 * @author ChuningLiu
 */
public class NoSuchBoardingPassException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchBoardingPassException</code>
     * without detail message.
     */
    public NoSuchBoardingPassException() {
    }

    /**
     * Constructs an instance of <code>NoSuchBoardingPassException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchBoardingPassException(String msg) {
        super(msg);
    }
}
