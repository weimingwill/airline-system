/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.FlightScheduleBookingClass;
import java.sql.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface SeatReallocationSessionLocal {
    
    public void reallocateSeats();
    //public List<SeatHistory> viewReallocationHitory();
    public void yieldManagement (List<Date> checkpoints);
    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses();
    
}
