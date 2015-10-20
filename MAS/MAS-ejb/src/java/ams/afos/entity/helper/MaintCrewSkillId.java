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
public class MaintCrewSkillId implements Serializable {

    @Column(name = "SYSTEMUSERID")
    private Long systemUserId;
    @Column(name = "MAINTCHECKTYPEID")
    private Long maintCheckTypeId;

    @Override
    public int hashCode() {
        return (int) (getSystemUserId() + getMaintCheckTypeId());
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the channelId fields are not set
        if (object instanceof MaintCrewSkillId) {
            MaintCrewSkillId otherId = (MaintCrewSkillId) object;
            return (otherId.getSystemUserId() == this.getSystemUserId()) && (otherId.getMaintCheckTypeId() == this.getMaintCheckTypeId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.MaintCrewSkillId[ systemUserId=" + getSystemUserId() + " ][ maintCheckTypeId=" + getMaintCheckTypeId() + " ]";
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
     * @return the maintCheckTypeId
     */
    public Long getMaintCheckTypeId() {
        return maintCheckTypeId;
    }

    /**
     * @param maintCheckTypeId the maintCheckTypeId to set
     */
    public void setMaintCheckTypeId(Long maintCheckTypeId) {
        this.maintCheckTypeId = maintCheckTypeId;
    }

}
