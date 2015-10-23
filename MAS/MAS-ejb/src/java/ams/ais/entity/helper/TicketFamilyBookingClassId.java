/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity.helper;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

/**
 *
 * @author winga_000
 */
@Embeddable
public class TicketFamilyBookingClassId implements Serializable{
    
    @JoinColumns({
        @JoinColumn(name = "AIRCRAFTID", referencedColumnName = "AIRCRAFTID"),
        @JoinColumn(name = "CABINCLASSID", referencedColumnName = "CABINCLASSID"),
        @JoinColumn(name = "TICKETFAMILYID", referencedColumnName = "TICKETFAMILYID")
    })
    private CabinClassTicketFamilyId cabinClassTicketFamilyId;
    @Column(name = "BOOKINGCLASSID")
    private Long bookingClassId;

    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }

    public CabinClassTicketFamilyId getCabinClassTicketFamilyId() {
        return cabinClassTicketFamilyId;
    }

    public void setCabinClassTicketFamilyId(CabinClassTicketFamilyId cabinClassTicketFamilyId) {
        this.cabinClassTicketFamilyId = cabinClassTicketFamilyId;
    }
    
    @Override
    public int hashCode() {
        return (int)(cabinClassTicketFamilyId.getAircraftCabinClassId().getAircraftId() + 
                cabinClassTicketFamilyId.getAircraftCabinClassId().getCabinClassId() + cabinClassTicketFamilyId.getTicketFamilyId() + bookingClassId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof CabinClassTicketFamilyId) {
      TicketFamilyBookingClassId otherId = (TicketFamilyBookingClassId) object;
      return (otherId.bookingClassId == this.bookingClassId) && (otherId.cabinClassTicketFamilyId == this.cabinClassTicketFamilyId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ bookingClassId=" + bookingClassId + " ][ cabinClassTicketFamilyId=" + cabinClassTicketFamilyId + " ]";
    }
    
}