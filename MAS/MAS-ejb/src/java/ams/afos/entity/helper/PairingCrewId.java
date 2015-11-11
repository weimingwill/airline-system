/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity.helper;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lewis
 */
@Embeddable
public class PairingCrewId implements Serializable {

    @Column(name = "SYSTEMUSERID")
    private Long systemUserId;
    @Column(name = "PAIRINGID")
    private Long pairingId;

    @Override
    public int hashCode() {
        return (int) (getSystemUserId() + getPairingId());
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the channelId fields are not set
        if (object instanceof PairingCrewId) {
            PairingCrewId otherId = (PairingCrewId) object;
            return (otherId.getSystemUserId() == this.getSystemUserId()) && (otherId.getPairingId() == this.getPairingId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.PairingCrewId[ systemUserId=" + getSystemUserId() + " ][ pairingId =" + getPairingId() + " ]";
    }

    /**
     * @return the systemUserId
     */
    public Long getSystemUserId() {
        return systemUserId;
    }

    /**
     * @param systemUserId the systemUserId to set
     */
    public void setSystemUserId(Long systemUserId) {
        this.systemUserId = systemUserId;
    }

    /**
     * @return the pairingId
     */
    public Long getPairingId() {
        return pairingId;
    }

    /**
     * @param pairingId the pairingId to set
     */
    public void setPairingId(Long pairingId) {
        this.pairingId = pairingId;
    }

}
