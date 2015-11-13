/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import ams.ais.entity.helper.FlightScheduleBookingClassId;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.Seat;
import ams.crm.entity.MktCampaign;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
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
@Table(name = "FLIGHTSCHEDULE_BOOKINGCLASS")
public class FlightScheduleBookingClass implements Serializable {

    @EmbeddedId
    private FlightScheduleBookingClassId flightScheduleBookingClassId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "FLIGHTSCHEDULEID", referencedColumnName = "FLIGHTSCHEDULEID")
    private FlightSchedule flightSchedule;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "BOOKINGCLASSID", referencedColumnName = "BOOKINGCLASSID")
    private BookingClass bookingClass;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SeatAllocationHistory> seatAllocationHistory = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<PhaseDemand> phaseDemands = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<MktCampaign> marketCampaigns = new ArrayList<>();

    @Column(name = "SEATQTY")
    private Integer seatQty;

    @Column(name = "SOLDSEATQTY")
    private Integer soldSeatQty;

    @Column(name = "PRICE")
    private Float price;

    @Column(name = "BASICPRICE")
    private Float basicPrice;

    @Column(name = "PRICECOEFFICIENT")
    private Float priceCoefficient;

    @Column(name = "DEMANDMEAN")
    private Float demandMean;

    @Column(name = "DEMANDDEV")
    private Float demandDev;

    @Column(name = "DELETED")
    private Boolean deleted;

    @Column(name = "PRICED")
    private Boolean priced;

    @Column(name = "CLOSED")
    private Boolean closed;

    public FlightScheduleBookingClassId getFlightScheduleBookingClassId() {
        return flightScheduleBookingClassId;
    }

    public void setFlightScheduleBookingClassId(FlightScheduleBookingClassId flightScheduleBookingClassId) {
        this.flightScheduleBookingClassId = flightScheduleBookingClassId;
    }

    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public Integer getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(Integer seatQty) {
        this.seatQty = seatQty;
    }

    public Integer getSoldSeatQty() {
        return soldSeatQty;
    }

    public void setSoldSeatQty(Integer soldSeatQty) {
        this.soldSeatQty = soldSeatQty;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<SeatAllocationHistory> getSeatAllocationHistory() {
        return seatAllocationHistory;
    }

    public void setSeatAllocationHistory(List<SeatAllocationHistory> seatAllocationHistory) {
        this.seatAllocationHistory = seatAllocationHistory;
    }

    public List<PhaseDemand> getPhaseDemands() {
        return phaseDemands;
    }

    public void setPhaseDemands(List<PhaseDemand> phaseDemands) {
        this.phaseDemands = phaseDemands;
    }

    public Float getDemandMean() {
        return demandMean;
    }

    public void setDemandMean(Float demandMean) {
        this.demandMean = demandMean;
    }

    public Float getDemandDev() {
        return demandDev;
    }

    public void setDemandDev(Float demandDev) {
        this.demandDev = demandDev;
    }

    public Float getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(Float basicPrice) {
        this.basicPrice = basicPrice;
    }

    public Boolean getPriced() {
        return priced;
    }

    public void setPriced(Boolean priced) {
        this.priced = priced;
    }

    /**
     * @return the marketCampaigns
     */
    public List<MktCampaign> getMarketCampaigns() {
        return marketCampaigns;
    }

    /**
     * @param marketCampaigns the marketCampaigns to set
     */
    public void setMarketCampaigns(List<MktCampaign> marketCampaigns) {
        this.marketCampaigns = marketCampaigns;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
