/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.exception;

/**
 *
 * @author weiming
 */
public class InvalidPromoCodeException extends Exception {

    /**
     * Creates a new instance of <code>PromoCodeExpireException</code> without
     * detail message.
     */
    public InvalidPromoCodeException() {
    }

    /**
     * Constructs an instance of <code>PromoCodeExpireException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPromoCodeException(String msg) {
        super(msg);
    }
}
