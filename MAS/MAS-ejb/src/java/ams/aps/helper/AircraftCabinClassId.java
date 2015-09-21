/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.helper;

import java.io.Serializable;

/**
 *
 * @author winga_000
 */
public class AircraftCabinClassId implements Serializable{
    private Long aircraftId;
    private Long cabinClassId;    
        
    @Override
    public int hashCode() {
        return (int)(aircraftId + cabinClassId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof AircraftCabinClassId) {
      AircraftCabinClassId otherId = (AircraftCabinClassId) object;
      return (otherId.cabinClassId == this.cabinClassId) && (otherId.aircraftId == this.aircraftId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.AircraftCabinClassId[ cabinClassId=" + cabinClassId + " ][ aircraftId=" + aircraftId + " ]";
    }
    
}