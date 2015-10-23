/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm_entity.helper;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author weiming
 */
@Embeddable
public class PrivilegeValueId implements Serializable{
        
    @Column(name = "MEMBERSHIP")
    private Long mebershipId;
    @Column(name = "PRIVILEGEID")
    private Long privilegeId;
    
    @Override
    public int hashCode() {
        return (int)(mebershipId + privilegeId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the mebershipId fields are not set
    if (object instanceof PrivilegeValueId) {
      PrivilegeValueId otherId = (PrivilegeValueId) object;
      return (otherId.privilegeId == this.privilegeId) && (otherId.mebershipId == this.mebershipId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ privilegeId=" + privilegeId + " ][ mebershipId=" + mebershipId + " ]";
    }

    public Long getAirTicketId() {
        return mebershipId;
    }

    public void setAirTicketId(Long mebershipId) {
        this.mebershipId = mebershipId;
    }

    public Long getAddOnId() {
        return privilegeId;
    }

    public void setAddOnId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }
    
    
}
