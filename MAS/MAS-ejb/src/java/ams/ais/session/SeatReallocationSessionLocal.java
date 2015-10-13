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
import ams.ais.util.exception.NeedBookingClassException;
import ams.ais.util.exception.NoSuchPhaseDemandException;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
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

    public void yieldManagement();

    public List<FlightScheduleBookingClass> getAllFlightScheduleBookingClasses();

    public List<NormalDistribution> getAllNormalDistributions();

    public void addPhaseDemand(FlightScheduleBookingClass flightScheduleBookingClass, float daysBeforeDeparture, float demandMean, float demandDev) throws ExistSuchCheckPointException;

    public void deletePhaseDemand(FlightScheduleBookingClass flightScheduleBookingClass, PhaseDemand phaseDemand) throws NoSuchPhaseDemandException;

    public void validatePhaseDemand(int daysBeforeDeparture, FlightScheduleBookingClass f) throws ExistSuchCheckPointException;

    public FlightScheduleBookingClass getFlightScheduleBookingClassbyFlightScheduleIDandBookingClassID(Long flightScheduleID, Long bookingClassID);

    public void reallocateSeatsforBookingClass(FlightScheduleBookingClass flightScheduleBookingClass, float demandMean, float demandDev)
            throws NoSuchFlightScheduleBookingClassException;

    public List<SeatAllocationHistory> getBookingClassSeatAllocationHistory(FlightScheduleBookingClass flightScheduleBookingClass);

    public List<PhaseDemand> getPhaseDemands(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers)
            throws NoSuchFlightSchedulException, NoSuchFlightScheduleBookingClassException, NeedBookingClassException;

    public PhaseDemand getPhaseDemandbyId(Long id);

//    public PhaseDemand getPhaseDemandByDays(Long flightScheduleId, List<FlightSchCabinClsTicFamBookingClsHelper> helpers, int daysBeforeDeparture) throws NoSuchPhaseDemandException;
}
