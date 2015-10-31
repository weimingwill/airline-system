/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity;

import ams.crm.entity.helper.Phone;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Bowen
 */
@Entity
public class Booking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updatedTime;
    private String referenceNo;
    private String email;
    private String eTicketNo;
    @Embedded
    private Phone phoneNo;
    private Double price;
    private Boolean paid;
    private String channel;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private BookingTrans bookingTrans;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "booking")
    private List<AirTicket> airTickets = new ArrayList<>();

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.Booking[ id=" + id + " ]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Phone getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Phone phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BookingTrans getBookingTrans() {
        return bookingTrans;
    }

    public void setBookingTrans(BookingTrans bookingTrans) {
        this.bookingTrans = bookingTrans;
    }

    public List<AirTicket> getAirTickets() {
        return airTickets;
    }

    public void setAirTickets(List<AirTicket> airTickets) {
        this.airTickets = airTickets;
    }

    /**
     * @return the eTicketNo
     */
    public String geteTicketNo() {
        return eTicketNo;
    }

    /**
     * @param eTicketNo the eTicketNo to set
     */
    public void seteTicketNo(String eTicketNo) {
        this.eTicketNo = eTicketNo;
    }
}
