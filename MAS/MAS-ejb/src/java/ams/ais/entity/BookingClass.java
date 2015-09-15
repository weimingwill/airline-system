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
public class BookingClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BookingClassId;
    private String name;
    private int seatQty;
    private float price;
    private float priceCoefficient;
    private int demand;
    private String status; 

    public Long getBookingClassId() {
        return BookingClassId;
    }

    public void setBookingClassId(Long BookingClassId) {
        this.BookingClassId = BookingClassId;
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
        hash += (BookingClassId != null ? BookingClassId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the BookingClassId fields are not set
        if (!(object instanceof BookingClass)) {
            return false;
        }
        BookingClass other = (BookingClass) object;
        if ((this.BookingClassId == null && other.BookingClassId != null) || (this.BookingClassId != null && !this.BookingClassId.equals(other.BookingClassId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.BookingClass[ BookingClassId=" + BookingClassId + " ]";
    }
    
}
