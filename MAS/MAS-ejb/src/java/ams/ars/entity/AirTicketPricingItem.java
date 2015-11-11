/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity;

import ams.ars.entity.helper.AirTicketPricingItemId;
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
public class AirTicketPricingItem implements Serializable {
    
    @EmbeddedId
    private AirTicketPricingItemId airTicketPricingItemId;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "AIRTICKETID", referencedColumnName = "ID")
    private AirTicket airTicket;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "PRICINGITEMID", referencedColumnName = "ID")
    private PricingItem pricingItem;
    
    @Column(name = "PRICE")
    private Double price;

    public AirTicketPricingItemId getAirTicketPricingItemId() {
        return airTicketPricingItemId;
    }

    public void setAirTicketPricingItemId(AirTicketPricingItemId airTicketPricingItemId) {
        this.airTicketPricingItemId = airTicketPricingItemId;
    }

    public AirTicket getAirTicket() {
        return airTicket;
    }

    public void setAirTicket(AirTicket airTicket) {
        this.airTicket = airTicket;
    }

    public PricingItem getPricingItem() {
        return pricingItem;
    }
 
    public void setPricingItem(PricingItem pricingItem) {
        this.pricingItem = pricingItem;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    
}
 