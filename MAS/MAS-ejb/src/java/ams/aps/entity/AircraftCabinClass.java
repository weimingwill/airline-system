/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import ams.ais.entity.CabinClass;
import ams.aps.helper.AircraftCabinClassId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@IdClass(AircraftCabinClassId.class)
@Table(name = "AIRCRAFT_CABINCLASS")
public class AircraftCabinClass implements Serializable {
    @Id
    private Long aircraftId;
    @Id 
    private Long cabinClassId;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "AIRCRAFTID", referencedColumnName="AIRCRAFTID")
    private Aircraft aircraft;
    
    @OneToOne
    @PrimaryKeyJoinColumn(name = "CABINCLASSID", referencedColumnName="CABINCLASSID")
    private CabinClass cabinClass;

    @Column(name = "SEATQTY") 
    private int seatQty;

    public Long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }

    public Long getCabinClassId() {
        return cabinClassId;
    }

    public void setCabinClassId(Long cabinClassId) {
        this.cabinClassId = cabinClassId;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }
    
    

}
