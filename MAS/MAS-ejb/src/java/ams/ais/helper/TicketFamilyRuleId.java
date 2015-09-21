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
public class TicketFamilyRuleId implements Serializable{
    private Long ticketFamilyId;
    private Long ruleId;

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
    
        
    @Override
    public int hashCode() {
        return (int)(ticketFamilyId + ruleId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof CabinClassTicketFamilyId) {
      TicketFamilyRuleId otherId = (TicketFamilyRuleId) object;
      return (otherId.ruleId == this.ruleId) && (otherId.ticketFamilyId == this.ticketFamilyId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.CabinClassTicketFamily[ ruleId=" + ruleId + " ][ ticketFamilyId=" + ticketFamilyId + " ]";
    }
    
}