/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ais.session;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.NormalDistribution;
import ams.ais.entity.PhaseDemand;
import ams.ais.entity.SeatAllocationHistory;
import ams.ais.util.exception.ExistSuchCheckPointException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import java.sql.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface SeatReallocationSessionLocal {
    
//    public void reallocateSeats();
    public List<SeatAllocationHistory> getAllReallocationHitorys();
    public void yieldManagement ();
    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses();
    public List<NormalDistribution> getAllNormalDistributions();
    public void reallocateBookingClassSeats (FlightScheduleBookingClass f, float demandMean, float demandDev);
    public List<PhaseDemand> getAllPhaseDemands();
    public void addPhaseDemand(FlightScheduleBookingClass f, int daysBeforeDeparture, float demandMean, float demandDev) throws ExistSuchCheckPointException;
    public void deletePhaseDemand(int daysBeforeDeparture) throws NoSuchPhaseDemandException;
    public PhaseDemand searchPhaseDemand(int daysBeforeDeparture) throws NoSuchPhaseDemandException;
    public void validatePhaseDemand(int daysBeforeDeparture, FlightScheduleBookingClass f) throws ExistSuchCheckPointException;
    

    
}
