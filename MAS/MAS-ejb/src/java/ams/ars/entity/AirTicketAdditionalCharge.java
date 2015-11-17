/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity;

import ams.ars.entity.helper.AirTicketAdditionalChargeId;
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
public class AirTicketAdditionalCharge implements Serializable {

    @EmbeddedId
    private AirTicketAdditionalChargeId airTicketAdditonalChargeId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "AIRTICKETID", referencedColumnName = "ID")
    private AirTicket airTicket;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "ADDITIONALCHARGEID", referencedColumnName = "ID")
    private AdditionalCharge additionalCharge;

    @Column(name = "PRICE")
    private Double price;

    public AirTicketAdditionalChargeId getAirTicketAdditonalChargeId() {
        return airTicketAdditonalChargeId;
    }

    public void setAirTicketAdditonalChargeId(AirTicketAdditionalChargeId airTicketAdditonalChargeId) {
        this.airTicketAdditonalChargeId = airTicketAdditonalChargeId;
    }

    public AirTicket getAirTicket() {
        return airTicket;
    }

    public void setAirTicket(AirTicket airTicket) {
        this.airTicket = airTicket;
    }

    public AdditionalCharge getAdditionalCharge() {
        return additionalCharge;
    }

    public void setAdditionalCharge(AdditionalCharge additionalCharge) {
        this.additionalCharge = additionalCharge;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
 