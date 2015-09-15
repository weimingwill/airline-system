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

/**
 *
 * @author Tongtong
 */
@Entity
public class BookingClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingClassId;
    private String name;
    private int seatQty;
    private float price;
    private float priceCoefficient;
    private int demand;
    private String status; 
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private TicketFamily ticketFamily = new TicketFamily();
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<Channel> channels = new ArrayList<>();

    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }


    public TicketFamily getTicketFamily() {
        return ticketFamily;
    }

    public void setTicketFamily(TicketFamily ticketFamily) {
        this.ticketFamily = ticketFamily;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(float priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    

  

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingClassId != null ? bookingClassId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the bookingClassId fields are not set
        if (!(object instanceof BookingClass)) {
            return false;
        }
        BookingClass other = (BookingClass) object;
        if ((this.bookingClassId == null && other.bookingClassId != null) || (this.bookingClassId != null && !this.bookingClassId.equals(other.bookingClassId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.BookingClass[ bookingClassId=" + bookingClassId + " ]";
    }
    
}
