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
public class TicketFamilyDetailId implements Serializable{
    private Long cabinClassId;
    private Long ticketFamilyId;
    
        
    @Override
    public int hashCode() {
        return (int)(cabinClassId + ticketFamilyId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof TicketFamilyDetailId) {
      TicketFamilyDetailId otherId = (TicketFamilyDetailId) object;
      return (otherId.cabinClassId == this.cabinClassId) && (otherId.ticketFamilyId == this.ticketFamilyId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.TicketFamilyDetail[ cabinClassId=" + cabinClassId + " ][ ticketFamilyId=" + ticketFamilyId + " ]";
    }
    
}