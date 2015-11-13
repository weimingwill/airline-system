/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity.helper;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winga_000
 */
@Embeddable
public class FlightScheduleBookingClassId implements Serializable{
    
    @Column(name = "FLIGHTSCHEDULEID")
    private Long flightScheduleId;
    @Column(name = "BOOKINGCLASSID")
    private Long bookingClassId;

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }
    
    
        
    @Override
    public int hashCode() {
        return (int)(flightScheduleId + bookingClassId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightScheduleId fields are not set
    if (object instanceof FlightScheduleBookingClassId) {
      FlightScheduleBookingClassId otherId = (FlightScheduleBookingClassId) object;
      return (Objects.equals(otherId.bookingClassId, this.bookingClassId)) && (Objects.equals(otherId.flightScheduleId, this.flightScheduleId));
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ bookingClassId=" + bookingClassId + " ][ flightScheduleId=" + flightScheduleId + " ]";
    }
    
}