/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.entity.helper;

import java.util.List;

/**
 *
 * @author ChuningLiu
 */
public class PassengerDisplayHelper {
    
    private String name;
    private String passport;
    private List<AirTicketDisplayHelper> legs;
    
    public PassengerDisplayHelper() {
        
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the passport
     */
    public String getPassport() {
        return passport;
    }

    /**
     * @param passport the passport to set
     */
    public void setPassport(String passport) {
        this.passport = passport;
    }

    /**
     * @return the legs
     */
    public List<AirTicketDisplayHelper> getLegs() {
        return legs;
    }

    /**
     * @param legs the legs to set
     */
    public void setLegs(List<AirTicketDisplayHelper> legs) {
        this.legs = legs;
    }

    
    
}
