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
public class LuggageNotRomovedException extends Exception {

    /**
     * Creates a new instance of <code>LuggageNotRomovedException</code> without
     * detail message.
     */
    public LuggageNotRomovedException() {
    }

    /**
     * Constructs an instance of <code>LuggageNotRomovedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public LuggageNotRomovedException(String msg) {
        super(msg);
    }
}
