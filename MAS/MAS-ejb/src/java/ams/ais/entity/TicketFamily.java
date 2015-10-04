/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author winga_000
 */
@Entity
public class TicketFamily implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketFamilyId;
    private String type;
    private String name;
    private boolean deleted;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "ticketFamily")
    private List<TicketFamilyRule> ticketFamilyRules = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private CabinClass cabinClass;

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }

    public List<TicketFamilyRule> getTicketFamilyRules() {
        return ticketFamilyRules;
    }

    public void setTicketFamilyRules(List<TicketFamilyRule> ticketFamilyRules) {
        this.ticketFamilyRules = ticketFamilyRules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        hash += (ticketFamilyId != null ? ticketFamilyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ticketFamilyId fields are not set
        if (!(object instanceof TicketFamily)) {
            return false;
        }
        TicketFamily other = (TicketFamily) object;
        if ((this.ticketFamilyId == null && other.ticketFamilyId != null) || (this.ticketFamilyId != null && !this.ticketFamilyId.equals(other.ticketFamilyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.TicketFamily[ ticketFamilyId=" + ticketFamilyId + " ]";
    }

}
