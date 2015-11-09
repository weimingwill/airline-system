/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.afos.session;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.Checklist;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.FlightDuty;
import ams.aps.entity.AircraftCabinClass;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Lewis
 */
@Stateless
public class FlightCrewMgmtSession implements FlightCrewMgmtSessionLocal {

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;

    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;

    @Override
    public List<FlightDuty> generateFlightDuties() {
        double minDutyPeriod = 10;
        double maxDutyPeriod = 14; // data is going to be get from Database (FlightCrewPosition)
        maxDutyPeriod = maxDutyPeriod < 14 ? 14 : maxDutyPeriod;

        List<FlightSchedule> nextMonthFlights = getNextMonthFlightSchedule();
        List<FlightSchedule> scheduleWithSameFlightNo;
        for (int i = 0; i < nextMonthFlights.size(); i++) {
            scheduleWithSameFlightNo = getScheduleWithSameFlightNo(nextMonthFlights.get(i));
           
        }

//        for (int i = 0; i < nextMonthFlights.size(); i++) {
//            for (int j = i + 1; j < nextMonthFlights.size(); j++) {
//                FlightSchedule schedule1 = nextMonthFlights.get(i);
//                FlightSchedule schedule2 = nextMonthFlights.get(j);
//                double schedule1Dur = (schedule1.getArrivalDate().getTime() - schedule1.getDepartDate().getTime()) / (60 * 60 * 1000.0) % 24.0;
//                double schedule2Dur = (schedule2.getArrivalDate().getTime() - schedule2.getDepartDate().getTime()) / (60 * 60 * 1000.0) % 24.0;
//                double totalDuration = schedule1Dur + schedule2Dur;
//                
//                if (schedule1.getFlight().getFlightNo().equals(schedule2.getFlight().getFlightNo()) && (totalDuration >= minDutyPeriod && totalDuration <= maxDutyPeriod)){
//                    FlightDuty dp = new FlightDuty();
//                    dp.setSitTimeInHrs(0.0);
//                    dp.setFlightSchedules(nextMonthFlights.subList(i, i));
//                    dp.setFlyingTimeInHrs(totalDuration);
//                    dp.setFlyingDistInKm(routePlanningSession.distance(schedule1.getLeg().getDepartAirport(), schedule1.getLeg().getArrivalAirport())/1000.0);
//                    setCrewQuota(dp, schedule1);
//                    break;
//                }
//            }
//
//        }
        return new ArrayList();
    }

    private List<FlightSchedule> getScheduleWithSameFlightNo(FlightSchedule flightSchedule) {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flight.flightNo LIKE :flightNo AND fs.flightScheduleId <> :id");
        query.setParameter("flightNo", flightSchedule.getFlight().getFlightNo());
        query.setParameter("id", flightSchedule.getFlightScheduleId());
        try {
            return (List<FlightSchedule>) query.getResultList();
        } catch (Exception ex) {
            return new ArrayList();
        }
    }

    private void setCrewQuota(FlightDuty dp, FlightSchedule schedule) {
        double totalFlyingTime = dp.getFlyingTimeInHrs();
        if (totalFlyingTime > 0) {
            int numCabinCrew = 0;
            int numCockpitCrew = 2;
            int totalPassenger = 0;
            for (AircraftCabinClass thisACC : schedule.getAircraft().getAircraftCabinClasses()) {
                totalPassenger += thisACC.getSeatQty();
            }

            if (totalPassenger <= 50) {
                numCabinCrew = 1;
            } else if (totalPassenger > 50 && totalPassenger < 100) {
                numCabinCrew = 2;
            } else {
                numCabinCrew = 2 + totalPassenger / 50;
            }

            if (totalFlyingTime > 8 && totalFlyingTime <= 12) {
                numCabinCrew = (int) Math.floor(numCabinCrew * 1.5);
                numCockpitCrew += 1;
            } else if (totalFlyingTime > 12) {
                numCabinCrew = numCabinCrew * 2;
                numCockpitCrew += 2;
            }
            dp.setCabinCrewQuota(numCabinCrew);
            dp.setCockpitCrewQuota(numCockpitCrew);
        }

    }

    private List<FlightSchedule> getNextMonthFlightSchedule() {
        Date nextMonthFirstDay = getNextMonthFirstDay();
        Date nextMonthLastDay = getNextMonthLastDay();
//        System.out.println("FlightCrewMgmtSession: nextMonthFirstDay = " + nextMonthFirstDay);
//        System.out.println("FlightCrewMgmtSession: nextMonthLastDay = " + nextMonthLastDay);

        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departDate BETWEEN :nextMonthFirstDay AND :nextMonthLastDay AND fs.deleted = FALSE ORDER BY fs.departDate ASC, fs.flight.flightNo ASC");
        query.setParameter("nextMonthFirstDay", nextMonthFirstDay);
        query.setParameter("nextMonthLastDay", nextMonthLastDay);

        try {
            List<FlightSchedule> nextMonthFlights;
            nextMonthFlights = (List<FlightSchedule>) query.getResultList();
            return nextMonthFlights;
        } catch (Exception ex) {
            return new ArrayList();
        }
    }

    private Date getNextMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, -12);
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    private Date getNextMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, calendar.getActualMaximum(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public void startBiddingSession(BiddingSession session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendBiddingSession(BiddingSession session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BiddingSession> getAllBiddingSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void assignPairingsToCrew(FlightCrew thisCrew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createFlightDutyChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFlightDutyChecklist(Checklist checklist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAttendance(List<FlightCrew> flightCrews) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FlightCrew> getOnDutyCrews(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getFlightDutyChecklist(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getChecklistTemplates(String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Checklist> getPostFlightReport(FlightSchedule flightSchedule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
