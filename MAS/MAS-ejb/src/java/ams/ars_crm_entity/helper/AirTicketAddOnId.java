/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm_entity.helper;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author weiming
 */
@Embeddable
public class AirTicketAddOnId {
        
    @Column(name = "AIRTICKETID")
    private Long airTicketId;
    @Column(name = "ADDONID")
    private Long addOnId;
    
        
    @Override
    public int hashCode() {
        return (int)(airTicketId + addOnId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the airTicketId fields are not set
    if (object instanceof AirTicketAddOnId) {
      AirTicketAddOnId otherId = (AirTicketAddOnId) object;
      return (otherId.addOnId == this.addOnId) && (otherId.airTicketId == this.airTicketId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ addOnId=" + addOnId + " ][ airTicketId=" + airTicketId + " ]";
    }

    public Long getAirTicketId() {
        return airTicketId;
    }

    public void setAirTicketId(Long airTicketId) {
        this.airTicketId = airTicketId;
    }

    public Long getAddOnId() {
        return addOnId;
    }

    public void setAddOnId(Long addOnId) {
        this.addOnId = addOnId;
    }
    
    
}
