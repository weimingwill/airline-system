/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.helper;

import java.io.Serializable;

/**
 *
 * @author winga_000
 */
public class BookingClassChannelId implements Serializable {

    private Long bookingClassId;
    private Long channelId;

    public Long getBookingClassId() {
        return bookingClassId;
    }

    public void setBookingClassId(Long bookingClassId) {
        this.bookingClassId = bookingClassId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    
    
    @Override
    public int hashCode() {
        return (int) (channelId + bookingClassId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the channelId fields are not set
        if (object instanceof CabinClassTicketFamilyId) {
            BookingClassChannelId otherId = (BookingClassChannelId) object;
            return (otherId.bookingClassId == this.bookingClassId) && (otherId.channelId == this.channelId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ams.ais.entity.BookingClassChannel[ channelId=" + channelId + " ][ bookingClassId=" + bookingClassId + " ]";
    }

}
