/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.entity.helper;

/**
 *
 * @author Bowen
 */
public class MilesRedemption {
   
    private String origin;
    private String destination;
    private Double miles;
    private String promoCode;

    
    
    public MilesRedemption(String origin, String destination,double miles,String promoCode) {
        this.origin = origin;
        this.destination = destination;
        this.miles=miles;
        this.promoCode=promoCode;
        
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getMiles() {
        return miles;
    }

    public void setMiles(Double miles) {
        this.miles = miles;
    }
    
    
    
}
