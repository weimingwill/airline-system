/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.dcs.session;

import ams.aps.entity.FlightSchedule;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.ars.entity.AirTicket;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.entity.Luggage;
import ams.dcs.util.exception.BoardingErrorException;
import ams.dcs.util.exception.NoSuchBoardingPassException;
import ams.dcs.util.exception.NoSuchPNRException;
import java.util.Date;
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

    public Double calculateLuggagePrice(AirTicket airTicket, List<CheckInLuggage> luggageList);

    public boolean checkInLuggage(AirTicket airticket, List<CheckInLuggage> luggageList, Double price);

    public AirTicket getAirTicketByPassID(long passID) throws NoSuchBoardingPassException;

    public void boardPassenger(AirTicket airTicket) throws BoardingErrorException;
    
    public AirTicket searchTicketByID(long ticketID)throws NoSuchPNRException;
}
