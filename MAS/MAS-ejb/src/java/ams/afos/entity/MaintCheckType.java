/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Lewis
 */
@Entity
public class MaintCheckType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintCheckTypeId;
    private String checkType;
    private String description;
    private Double estimatedTime;

    public Long getMaintCheckTypeId() {
        return maintCheckTypeId;
    }

    public void setMaintCheckTypeId(Long maintCheckTypeId) {
        this.maintCheckTypeId = maintCheckTypeId;
    }

    /**
     * @return the checkType
     */
    public String getCheckType() {
        return checkType;
    }

    /**
     * @param checkType the checkType to set
     */
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the estimatedTime
     */
    public Double getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * @param estimatedTime the estimatedTime to set
     */
    public void setEstimatedTime(Double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (maintCheckTypeId != null ? maintCheckTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the maintCheckTypeId fields are not set
        if (!(object instanceof MaintCheckType)) {
            return false;
        }
        MaintCheckType other = (MaintCheckType) object;
        if ((this.maintCheckTypeId == null && other.maintCheckTypeId != null) || (this.maintCheckTypeId != null && !this.maintCheckTypeId.equals(other.maintCheckTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.afos.entity.MaintCheckType[ maintCheckTypeId=" + maintCheckTypeId + " ]";
    }
}
