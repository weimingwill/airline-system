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
import java.util.Arrays;
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
        List<FlightSchedule> nextMonthFlights = getNextMonthFlightSchedule();

        for (FlightSchedule thisSchedule : nextMonthFlights) {
            System.out.print("generateFlightDuties(): Start create new FlightDuty\n");
            FlightDuty newFlightDuty = new FlightDuty();

            if (thisSchedule.getPreFlightSched() == null 
                    || (thisSchedule.getPreFlightSched() != null && !thisSchedule.getFlight().getFlightNo().equals(thisSchedule.getPreFlightSched().getFlight().getFlightNo()))) { 
                //if first flight segment of flight
                System.out.println("\tFound first flight segment of flight");
                printFlightInfo(thisSchedule);
                newFlightDuty.setFlightSchedules(getFlightDutyFlights(thisSchedule));
                setFlightDutyInfo(newFlightDuty);
                setCrewQuota(newFlightDuty, thisSchedule);
            }     
        }
        return new ArrayList();

    }

    private void setFlightDutyInfo(FlightDuty newFlightDuty){
        Double totalFlyingTime = 0.0;
        Double totalFlyingDist = 0.0;
        Double totalSitTime = 0.0;
        List<FlightSchedule> flightSchedules = newFlightDuty.getFlightSchedules();
        for(int i = 0 ; i < flightSchedules.size();i++){
            if(i != flightSchedules.size()-1){
                totalSitTime += flightSchedules.get(i).getTurnoverTime();
            }
            totalFlyingDist += routePlanningSession.distance(flightSchedules.get(i).getLeg().getDepartAirport(), flightSchedules.get(i).getLeg().getArrivalAirport());
            totalFlyingTime += flightSchedules.get(i).getArrivalDate().getTime() - flightSchedules.get(i).getDepartDate().getTime();
        }
        totalFlyingTime *= 2.77778e-7;
        totalFlyingDist /= 1000;
        newFlightDuty.setFlyingDistInKm(totalFlyingDist);
        newFlightDuty.setFlyingTimeInHrs(totalFlyingTime);
        newFlightDuty.setSitTimeInHrs(totalSitTime);
        System.out.println("setFlightDutyInfo(): \n\tTotal Flying Dist = " + totalFlyingDist + "\n\tTotal Flying Hours = " + totalFlyingTime + "\n\tTotal Sit Time = " + totalSitTime);
    }
    
    private void printFlightInfo(FlightSchedule thisSchedule) {
        System.out.println("thisSchedule: \n\tFlight No: " + thisSchedule.getFlight().getFlightNo()
                + "\n\tFrom: " + thisSchedule.getLeg().getDepartAirport().getAirportName()
                + "\n\tTo: " + thisSchedule.getLeg().getArrivalAirport().getAirportName()
                + "\n\tDeparture Time: " + thisSchedule.getDepartDate()
                + "\n\tArrival Time: " + thisSchedule.getArrivalDate() + "\n\n");
    }

    private List<FlightSchedule> getFlightDutyFlights(FlightSchedule thisSchedule) {
        FlightSchedule currSchedule = thisSchedule;
        FlightSchedule nextSchedule = thisSchedule.getNextFlightSched();
        String thisFlightNo, nextFlightNo;
        List<FlightSchedule> flightDutyFlightSchedules = new ArrayList();

        if (nextSchedule == null) {
            flightDutyFlightSchedules.add(currSchedule);
            return flightDutyFlightSchedules;
        } else {
            do {
                thisFlightNo = currSchedule.getFlight().getFlightNo();
                nextFlightNo = nextSchedule.getFlight().getFlightNo();
                flightDutyFlightSchedules.add(currSchedule);
                currSchedule = nextSchedule;
                nextSchedule = nextSchedule.getNextFlightSched();
            } while (nextSchedule != null && nextFlightNo.equals(thisFlightNo));
            return flightDutyFlightSchedules;
        }
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
            System.out.println("setCrewQuota(): \n\tNumber of Passengers = " + totalPassenger + "\n\tNumber of Cabin Crew = " + numCabinCrew + "\n\tNumber of Cockpit Crew = " + numCockpitCrew);

        }

    }

    private List<FlightSchedule> getNextMonthFlightSchedule() {
        Date nextMonthFirstDay = getNextMonthFirstDay();
        Date nextMonthLastDay = getNextMonthLastDay();
        System.out.println("FlightCrewMgmtSession: nextMonthFirstDay = " + nextMonthFirstDay);
        System.out.println("FlightCrewMgmtSession: nextMonthLastDay = " + nextMonthLastDay);

        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departDate BETWEEN :nextMonthFirstDay AND :nextMonthLastDay AND fs.deleted = FALSE ORDER BY fs.departDate ASC, fs.flight.flightNo ASC");
        query.setParameter("nextMonthFirstDay", nextMonthFirstDay);
        query.setParameter("nextMonthLastDay", nextMonthLastDay);

        try {
            List<FlightSchedule> nextMonthFlights;
            nextMonthFlights = (List<FlightSchedule>) query.getResultList();
            System.out.println("nextMonthFlights = " + nextMonthFlights);
            return nextMonthFlights;
        } catch (Exception ex) {
            return new ArrayList();
        }
    }

    private Date getNextMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    private Date getNextMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
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
