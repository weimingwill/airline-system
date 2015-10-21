/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import ams.ais.entity.helper.CabinClassTicketFamilyId;
import ams.aps.entity.AircraftCabinClass;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@Table(name = "CABINCLASS_TICKETFAMILY")
public class CabinClassTicketFamily implements Serializable {

    @EmbeddedId
    private CabinClassTicketFamilyId cabinClassTicketFamilyId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "AIRCRAFTID", referencedColumnName = "AIRCRAFTID"),
        @PrimaryKeyJoinColumn(name = "CABINCLASSID", referencedColumnName = "CABINCLASSID")
    })
    private AircraftCabinClass aircraftCabinClass;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "TICKETFAMILYID", referencedColumnName = "TICKETFAMILYID")
    private TicketFamily ticketFamily;

    @Column(name = "SEATQTY")
    private Integer seatQty;
    
    @Column(name = "PRICE")
    private Float price;
    
    @Column(name = "PRICECOEFFICIENT")
    private Float priceCoefficient;
    
    @Column(name = "DELETED")
    private Boolean deleted;

    public CabinClassTicketFamilyId getCabinClassTicketFamilyId() {
        return cabinClassTicketFamilyId;
    }

    public void setCabinClassTicketFamilyId(CabinClassTicketFamilyId cabinClassTicketFamilyId) {
        this.cabinClassTicketFamilyId = cabinClassTicketFamilyId;
    }

    public AircraftCabinClass getAircraftCabinClass() {
        return aircraftCabinClass;
    }

    public void setAircraftCabinClass(AircraftCabinClass aircraftCabinClass) {
        this.aircraftCabinClass = aircraftCabinClass;
    }

    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public Integer getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(Integer seatQty) {
        this.seatQty = seatQty;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(Float priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }
}
