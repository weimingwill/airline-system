/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm.entity;

import ams.ars_crm_entity.helper.AirTicketAddOnId;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author weiming
 */
@Entity
public class AirTicketAddOn implements Serializable {
    @EmbeddedId
    private AirTicketAddOnId airTicketAddOnId;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "AIRTICKETID", referencedColumnName = "ID")
    private AirTicket airTicket;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "ADDONID", referencedColumnName = "ID")
    private AddOn addOn;
    
    @Column(name = "PRICE")
    private Double price;

    public AirTicketAddOnId getAirTicketAddOnId() {
        return airTicketAddOnId;
    }

    public void setAirTicketAddOnId(AirTicketAddOnId airTicketAddOnId) {
        this.airTicketAddOnId = airTicketAddOnId;
    }
    
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public AirTicket getAirTicket() {
        return airTicket;
    }

    public void setAirTicket(AirTicket airTicket) {
        this.airTicket = airTicket;
    }

    public AddOn getAddOn() {
        return addOn;
    }

    public void setAddOn(AddOn addOn) {
        this.addOn = addOn;
    }
    
}
