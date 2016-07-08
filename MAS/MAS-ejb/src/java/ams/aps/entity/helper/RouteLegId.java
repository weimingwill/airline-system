/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity.helper;

import java.io.Serializable;

/**
 *
 * @author Lewis
 */
public class RouteLegId implements Serializable{
    private Long routeId;
    private Long legId;    
        
    @Override
    public int hashCode() {
        return (int)(routeId + legId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
    if (object instanceof RouteLegId) {
      RouteLegId otherId = (RouteLegId) object;
      return (otherId.routeId == this.routeId) && (otherId.legId == this.legId);
    }
    return false;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.RouteLegId[ routeId=" + routeId + " ][ legId=" + legId + " ]";
    }
}
