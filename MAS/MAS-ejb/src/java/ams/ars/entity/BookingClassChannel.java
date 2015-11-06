/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars.entity;

import ams.ais.entity.BookingClass;
import ams.ais.entity.helper.BookingClassChannelId;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author winga_000
 */
@Entity
@Table(name = "BOOKINGCLASS_CHANNEL")
public class BookingClassChannel implements Serializable {
 
    @EmbeddedId
    private BookingClassChannelId bookingClassChannelId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "BOOKINGCLASSID", referencedColumnName = "BOOKINGCLASSID")
    private BookingClass bookingClass;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "CHANNELID", referencedColumnName = "CHANNELID")
    private Channel channel;

    @Column(name = "STATUS")
    private String status;

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BookingClassChannelId getBookingClassChannelId() {
        return bookingClassChannelId;
    }

    public void setBookingClassChannelId(BookingClassChannelId bookingClassChannelId) {
        this.bookingClassChannelId = bookingClassChannelId;
    }
    
}
