/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.helper;

import java.io.Serializable;

/**
 *
 * @author winga_000
 */
public class CabinClassTicketFamilyId implements Serializable{
    private Long cabinClassId;
    private Long ticketFamilyId;

    public Long getCabinClassId() {
        return cabinClassId;
    }

    public void setCabinClassId(Long cabinClassId) {
        this.cabinClassId = cabinClassId;
    }

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }
    
        
    
    @Override
    public int hashCode() {
        return (int)(cabinClassId + ticketFamilyId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof CabinClassTicketFamilyId) {
      CabinClassTicketFamilyId otherId = (CabinClassTicketFamilyId) object;
      return (otherId.cabinClassId == this.cabinClassId) && (otherId.ticketFamilyId == this.ticketFamilyId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ cabinClassId=" + cabinClassId + " ][ ticketFamilyId=" + ticketFamilyId + " ]";
    }
    
}