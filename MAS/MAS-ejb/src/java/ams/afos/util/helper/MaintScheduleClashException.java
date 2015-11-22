/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.util.helper;

/**
 *
 * @author Lewis
 */
public class MaintScheduleClashException extends Exception {

    /**
     * Creates a new instance of <code>MaintScheduleClashException</code>
     * without detail message.
     */
    public MaintScheduleClashException() {
    }

    /**
     * Constructs an instance of <code>MaintScheduleClashException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MaintScheduleClashException(String msg) {
        super(msg);
    }
}
