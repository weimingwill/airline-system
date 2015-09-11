/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class SeatConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer firstClassCount;
    private Integer bizClassCount;
    private Integer preEconClassCount;
    private Integer econClassCount;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SeatConfig)) {
            return false;
        }
        SeatConfig other = (SeatConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.SeatConfig[ id=" + id + " ]";
    }

    /**
     * @return the firstClassCount
     */
    public Integer getFirstClassCount() {
        return firstClassCount;
    }

    /**
     * @param firstClassCount the firstClassCount to set
     */
    public void setFirstClassCount(Integer firstClassCount) {
        this.firstClassCount = firstClassCount;
    }

    /**
     * @return the bizClassCount
     */
    public Integer getBizClassCount() {
        return bizClassCount;
    }

    /**
     * @param bizClassCount the bizClassCount to set
     */
    public void setBizClassCount(Integer bizClassCount) {
        this.bizClassCount = bizClassCount;
    }

    /**
     * @return the preEconClassCount
     */
    public Integer getPreEconClassCount() {
        return preEconClassCount;
    }

    /**
     * @param preEconClassCount the preEconClassCount to set
     */
    public void setPreEconClassCount(Integer preEconClassCount) {
        this.preEconClassCount = preEconClassCount;
    }

    /**
     * @return the econClassCount
     */
    public Integer getEconClassCount() {
        return econClassCount;
    }

    /**
     * @param econClassCount the econClassCount to set
     */
    public void setEconClassCount(Integer econClassCount) {
        this.econClassCount = econClassCount;
    }
    
}
