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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Bowen
 */
@Entity
public class AirTicket implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticketNo;
    
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Booking booking;
    
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "airTicket")
    private List<AirTicketAddOn> airTicketAddOns = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "airTicket")
    private List<AirTicketPricingItem> airTicketPricingItems = new ArrayList<>();
    
    @OneToOne(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Seat seat;
    
    @OneToOne(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private BoardingPass boardingPass;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AirTicket)) {
            return false;
        }
        AirTicket other = (AirTicket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.Airticket[ id=" + id + " ]";
    }
    
    
}
