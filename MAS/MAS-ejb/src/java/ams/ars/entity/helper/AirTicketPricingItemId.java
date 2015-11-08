/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity.helper;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author weiming
 */
@Embeddable
public class AirTicketPricingItemId {
    
    @Column(name = "AIRTICKETID")
    private Long airTicketId;
    @Column(name = "PRICINGITEMID")
    private Long pricingItemId;
    
        
    @Override
    public int hashCode() {
        return (int)(airTicketId + pricingItemId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the airTicketId fields are not set
    if (object instanceof AirTicketPricingItemId) {
      AirTicketPricingItemId otherId = (AirTicketPricingItemId) object;
      return (otherId.pricingItemId == this.pricingItemId) && (otherId.airTicketId == this.airTicketId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ pricingItemId=" + pricingItemId + " ][ airTicketId=" + airTicketId + " ]";
    }

    public Long getAirTicketId() {
        return airTicketId;
    }

    public void setAirTicketId(Long airTicketId) {
        this.airTicketId = airTicketId;
    }

    public Long getPricingItemId() {
        return pricingItemId;
    }

    public void setPricingItemId(Long pricingItemId) {
        this.pricingItemId = pricingItemId;
    }
}
