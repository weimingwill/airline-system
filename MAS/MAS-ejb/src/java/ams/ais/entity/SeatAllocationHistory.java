/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author winga_000
 */
@Entity
public class SeatAllocationHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long seatAllocationHistoryId;
    private int seatNoBefore;
    private int seatNoAfter;
    private Date allocateTime;
//  private int modified;
    private FlightScheduleBookingClass flightScheduleBookingClass = new FlightScheduleBookingClass();

    public Long getSeatAllocationHistoryId() {
        return seatAllocationHistoryId;
    }

    public void setSeatAllocationHistoryId(Long seatAllocationHistoryId) {
        this.seatAllocationHistoryId = seatAllocationHistoryId;
    }

    public int getSeatNoBefore() {
        return seatNoBefore;
    }

    public void setSeatNoBefore(int seatNoBefore) {
        this.seatNoBefore = seatNoBefore;
    }

    public int getSeatNoAfter() {
        return seatNoAfter;
    }

    public void setSeatNoAfter(int seatNoAfter) {
        this.seatNoAfter = seatNoAfter;
    }
    
    
    public Date getAllocateTime() {
        return allocateTime;
    }

    public void setAllocateTime(Date allocateTime) {
        this.allocateTime = allocateTime;
    }

//    public int getModified() {
//        return modified;
//    }
//
//    public void setModified(int modified) {
//        this.modified = modified;
//    }

    public FlightScheduleBookingClass getFlightScheduleBookingClass() {
        return flightScheduleBookingClass;
    }

    public void setFlightScheduleBookingClass(FlightScheduleBookingClass flightScheduleBookingClass) {
        this.flightScheduleBookingClass = flightScheduleBookingClass;
    }
    


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatAllocationHistoryId != null ? seatAllocationHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the seatAllocationHistoryId fields are not set
        if (!(object instanceof SeatAllocationHistory)) {
            return false;
        }
        SeatAllocationHistory other = (SeatAllocationHistory) object;
        if ((this.seatAllocationHistoryId == null && other.seatAllocationHistoryId != null) || (this.seatAllocationHistoryId != null && !this.seatAllocationHistoryId.equals(other.seatAllocationHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.SeatAllocationHistory[ seatAllocationHistoryId=" + seatAllocationHistoryId + " ]";
    }
    
}
