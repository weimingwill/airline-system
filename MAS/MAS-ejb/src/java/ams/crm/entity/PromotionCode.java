/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Bowen
 */
@Entity
public class PromotionCode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double percentage;
    private Double promoValue;
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private MktCampaign mktCampaign;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPromoValue() {
        return promoValue;
    }

    public void setPromoValue(Double promoValue) {
        this.promoValue = promoValue;
    }
    
    public MktCampaign getMktCampaign() {
        return mktCampaign;
    }

    public void setMktCampaign(MktCampaign mktCampaign) {
        this.mktCampaign = mktCampaign;
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
        if (!(object instanceof PromotionCode)) {
            return false;
        }
        PromotionCode other = (PromotionCode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.Promotion[ id=" + id + " ]";
    }
    
}
