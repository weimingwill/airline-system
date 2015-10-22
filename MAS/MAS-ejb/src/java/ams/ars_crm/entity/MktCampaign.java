/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Bowen
 */
@Entity
public class MktCampaign implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String name;
    private String description;
    private String campaignResult;
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy="mktCampaign")
    private List<PromotionCode> promotionCodes = new ArrayList<>();

   
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCampaignResult() {
        return campaignResult;
    }

    public void setCampaignResult(String campaignResult) {
        this.campaignResult = campaignResult;
    }
    
     public List<PromotionCode> getPromotionCodes() {
        return promotionCodes;
    }

    public void setPromotionCodes(List<PromotionCode> promotionCodes) {
        this.promotionCodes = promotionCodes;
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
        if (!(object instanceof MktCampaign)) {
            return false;
        }
        MktCampaign other = (MktCampaign) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.MktCampaign[ id=" + id + " ]";
    }
    
}
