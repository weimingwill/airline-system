/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.ais.entity.TicketFamilyRule;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AirTicket;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ChuningLiu
 */
@Local
public interface CheckInSessionLocal {

    public Customer getCustomerByPassport(String passportNo);

    public List<Luggage> getCustomerLuggages(Customer cust, FlightSchedule schedule);

    public List<AirTicket> getFSforCheckin(String passport);

    public String selectSeat(String ticketNo);

    public boolean checkInPassenger(AirTicket ticket);
            
    public boolean checkInuggage(ArrayList<AirTicket> airtickets, ArrayList<CheckInLuggage> luggageList);
}
