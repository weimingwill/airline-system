/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity.helper;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author weiming
 */
@Embeddable
public class AirTicketAdditionalChargeId implements Serializable {
    
    @Column(name = "AIRTICKETID")
    private Long airTicketId;
    @Column(name = "ADDITIONALCHARGEID")
    private Long additionalChargeId;
    
        
    @Override
    public int hashCode() {
        return (int)(airTicketId + additionalChargeId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the airTicketId fields are not set
    if (object instanceof AirTicketAdditionalChargeId) {
      AirTicketAdditionalChargeId otherId = (AirTicketAdditionalChargeId) object;
      return (otherId.additionalChargeId == this.additionalChargeId) && (otherId.airTicketId == this.airTicketId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ additionalChargeId=" + additionalChargeId + " ][ airTicketId=" + airTicketId + " ]";
    }

    public Long getAirTicketId() {
        return airTicketId;
    }

    public void setAirTicketId(Long airTicketId) {
        this.airTicketId = airTicketId;
    }

    public Long getAdditionalChargeId() {
        return additionalChargeId;
    }

    public void setAdditionalChargeId(Long additionalChargeId) {
        this.additionalChargeId = additionalChargeId;
    }

}
