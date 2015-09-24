/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.BookingClass;
import ams.ais.entity.FlightScheduleBookingClass;
import java.sql.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tongtong
 */
@Stateless
public class SeatReallocationSession implements SeatReallocationSessionLocal {

    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void reallocateSeats() {
        List<FlightScheduleBookingClass> flightScheduleBookingClasses = getAllFlightScheduleBookingClasses();
        for (FlightScheduleBookingClass flightScheduleBookingClass : flightScheduleBookingClasses) {
            int demand = flightScheduleBookingClass.getDemand();
            float price = flightScheduleBookingClass.getPrice();

        }

    }

    @Override
    public void yieldManagement(List<Date> checkpoints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses() {
        Query query = entityManager.createQuery("SELECT c FROM FlightScheduleBookingClass c");
        return query.getResultList();

    }

}
