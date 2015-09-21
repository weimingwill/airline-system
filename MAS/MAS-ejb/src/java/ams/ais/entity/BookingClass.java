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
 * @author Tongtong
 */
@Entity
public class BookingClass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingClassId;
    private String name;
    private boolean deleted;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "bookingClass")
    private List<BookingClassChannel> bookingClassChannels = new ArrayList<>();

    public void create(String name){
        this.setName(name);
        this.setDeleted(false);
    }
    
    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
