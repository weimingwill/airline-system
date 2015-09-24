/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Tongtong
 */
@Entity
public class NormalDistribution implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long normalDistributionId;
    private float dev;
    private float p;
    private boolean deleted;

    
    public Long getNormalDistributionId() {
        return normalDistributionId;
    }

    public void setNormalDistributionId(Long normalDistributionId) {
        this.normalDistributionId = normalDistributionId;
    }

    public float getDev() {
        return dev;
    }

    public void setDev(float dev) {
        this.dev = dev;
    }

    public float getP() {
        return p;
    }

    public void setP(float p) {
        this.p = p;
    }
    
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (normalDistributionId != null ? normalDistributionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the normalDistributionId fields are not set
        if (!(object instanceof NormalDistribution)) {
            return false;
        }
        NormalDistribution other = (NormalDistribution) object;
        if ((this.normalDistributionId == null && other.normalDistributionId != null) || (this.normalDistributionId != null && !this.normalDistributionId.equals(other.normalDistributionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.NormalDistribution[ normalDistributionId=" + normalDistributionId + " ]";
    }
    
}
