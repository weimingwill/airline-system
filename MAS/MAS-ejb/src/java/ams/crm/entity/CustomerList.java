/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Tongtong
 */
@Entity
public class CustomerList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdTime;
    private String description;
    
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private List<SelectedCust> selectedCusts;

    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy = "customerLists")
    private List<MktCampaign> mktCampaigns = new ArrayList<>();
            
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerList)) {
            return false;
        }
        CustomerList other = (CustomerList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.CustomerList[ id=" + id + " ]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SelectedCust> getSelectedCusts() {
        return selectedCusts;
    }

    public void setSelectedCusts(List<SelectedCust> selectedCusts) {
        this.selectedCusts = selectedCusts;
    }

    public List<MktCampaign> getMktCampaigns() {
        return mktCampaigns;
    }

    public void setMktCampaigns(List<MktCampaign> mktCampaigns) {
        this.mktCampaigns = mktCampaigns;
    }
    
}
