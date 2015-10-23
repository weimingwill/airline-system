/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.ars_crm.entity.AirTicket;
import ams.ars_crm.entity.Seat;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface CheckInSessionLocal {
    public List<AirTicket> getValidAirTicketsByCustomer(String passportNo);
    
    public Seat selectSeat(AirTicket airTicket);
    
    public boolean checkInPassenger();
}
