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
import javax.persistence.ManyToMany;
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
    private String name;
    private int seatQty;
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private CabinClass cabinClass = new CabinClass();
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "ticketFamily")
    private List<BookingClass> bookingClasses  = new ArrayList<>();    
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<Rule> rules = new ArrayList<>();
    
    public Long getTicketFamilyId() {
        return ticketFamilyId;
    }

    public void setTicketFamilyId(Long ticketFamilyId) {
        this.ticketFamilyId = ticketFamilyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeatQty() {
        return seatQty;
    }

    public void setSeatQty(int seatQty) {
        this.seatQty = seatQty;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public List<BookingClass> getBookingClasses() {
        return bookingClasses;
    }

    public void setBookingClasses(List<BookingClass> bookingClasses) {
        this.bookingClasses = bookingClasses;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
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
