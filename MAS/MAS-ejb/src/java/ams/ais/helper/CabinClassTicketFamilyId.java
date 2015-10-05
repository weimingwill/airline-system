/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.helper;

import ams.aps.helper.AircraftCabinClassId;
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
public class CabinClassTicketFamilyId implements Serializable {

    @JoinColumns({
        @JoinColumn(name = "AIRCRAFTID", referencedColumnName = "AIRCRAFTID"),
        @JoinColumn(name = "CABINCLASSID", referencedColumnName = "CABINCLASSID")
    })
    private AircraftCabinClassId aircraftCabinClassId;
    
    @Column(name = "TICKETFAMILYID")
    private Long ticketFamilyId;

    public AircraftCabinClassId getAircraftCabinClassId() {
        return aircraftCabinClassId;
    }

    public void setAircraftCabinClassId(AircraftCabinClassId aircraftCabinClassId) {
        this.aircraftCabinClassId = aircraftCabinClassId;
    }

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }

    @Override
    public int hashCode() {
        return (int) (aircraftCabinClassId.getAircraftId() + aircraftCabinClassId.getCabinClassId() + ticketFamilyId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
        if (object instanceof CabinClassTicketFamilyId) {
            CabinClassTicketFamilyId otherId = (CabinClassTicketFamilyId) object;
            return (otherId.aircraftCabinClassId == this.aircraftCabinClassId) && (otherId.ticketFamilyId == this.ticketFamilyId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ aircraftCabinClassId=" + aircraftCabinClassId + " ][ ticketFamilyId=" + ticketFamilyId + " ]";
    }

}
