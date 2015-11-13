/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import ams.ais.entity.CabinClass;
import ams.ais.entity.CabinClassTicketFamily;
import ams.aps.entity.helper.AircraftCabinClassId;
import ams.ars.entity.Seat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "AIRCRAFTID", referencedColumnName = "AIRCRAFTID")
    private Aircraft aircraft;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "CABINCLASSID", referencedColumnName = "CABINCLASSID")
    private CabinClass cabinClass;

    @Column(name = "SEATQTY")
    private Integer seatQty;

    @Column(name = "ROWNo")
    private Integer numRows;

    @Column(name = "COLNO")
    private Integer numCols;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "aircraftCabinClass")
    private List<CabinClassTicketFamily> cabinClassTicketFamilys = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<Seat> seats = new ArrayList<>();

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

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<CabinClassTicketFamily> getCabinClassTicketFamilys() {
        return cabinClassTicketFamilys;
    }

    public void setCabinClassTicketFamilys(List<CabinClassTicketFamily> cabinClassTicketFamilys) {
        this.cabinClassTicketFamilys = cabinClassTicketFamilys;
    }

    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer numRows) {
        this.numRows = numRows;
    }

    public Integer getNumCols() {
        return numCols;
    }

    public void setNumCols(Integer numCols) {
        this.numCols = numCols;
    }

    public Integer getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(Integer seatQty) {
        this.seatQty = seatQty;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
