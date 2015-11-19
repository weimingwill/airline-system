/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ars_crm;

import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.aps.util.helper.FlightSchedStatus;
import ams.crm.session.BookingSessionLocal;
import ams.crm.util.helper.FlightSchedHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.MsgController;

/**
 *
 * @author weiming
 */
@Named(value = "flightStatusBacking")
@ViewScoped
public class FlightStatusBacking implements Serializable {

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private BookingSessionLocal bookingSession;

    @Inject
    MsgController msgController;
    @Inject
    CrmExNavController crmExNavController;

    private List<Airport> allAirports = new ArrayList<>();

    private boolean selecteRoute = true;
    private String searchBy;
    private String scheduleChoice;
    private Date date;

    //Search by route
    private Airport deptAirport;
    private Airport arrAirport;

    //Search by fligth number
    private Airport airport;
    private String flightNo;

    //Search results;
    private List<FlightSchedHelper> flightSchedHelpers;
    private List<FlightSchedule> flightScheds = new ArrayList<>();
    private Map<Long, FlightSchedule> inDirectFlightSchedMaps = new HashMap<>();

    /**
     * Creates a new instance of FlightStatusBacking
     */
    public FlightStatusBacking() {
    }

    @PostConstruct
    public void init() {
        allAirports = routePlanningSession.getAllAirports();
        searchBy = "route";
        scheduleChoice = "depart";
    }

    //Auto complete departure airport when typing in.
    public List<Airport> completeDeptAirport(String query) {
        List<Airport> deptAirports = allAirports;
        deptAirports.remove(arrAirport);
        query = query.toLowerCase();
        return completeAirport(deptAirports, query);
    }

    //Auto complete arrive airport when typing in.
    public List<Airport> completeArrAirport(String query) {
        List<Airport> arrAirports = allAirports;
        arrAirports.remove(deptAirport);
        query = query.toLowerCase();
        return completeAirport(arrAirports, query);
    }

    //Auto complete airports when search by flight number
    public List<Airport> completeAirport(String query) {
        return completeAirport(allAirports, query);
    }

    //Auto complete airport.
    private List<Airport> completeAirport(List<Airport> allAirports, String query) {
        List<Airport> filteredAirports = new ArrayList<>();
        Set<Airport> hs = new HashSet<>();

        for (Airport airport : allAirports) {
            if (airport.getAirportName().toLowerCase().startsWith(query)
                    || airport.getCity().getCityName().toLowerCase().startsWith(query)
                    || airport.getCountry().getCountryName().toLowerCase().startsWith(query)) {
                hs.add(airport);
            }
        }
        filteredAirports.addAll(hs);
        return filteredAirports;
    }

    public void onSearchBySelected() {
        if ("flightNo".equals(searchBy)) {
            selecteRoute = false;
        } else {
            selecteRoute = true;
        }
    }

    public String searchForFlights() {
        try {
            flightSchedHelpers = new ArrayList<>();
            inDirectFlightSchedMaps = new HashMap<>();
            flightScheds = bookingSession.searchForOneWayFlights(deptAirport, arrAirport, date, inDirectFlightSchedMaps, FlightSchedStatus.RELEASE);
            for (FlightSchedule flightSched : flightScheds) {
                FlightSchedHelper flightSchedHelper = new FlightSchedHelper();
                flightSchedHelper.setFlightSched(flightSched);
                FlightSchedule nextFlightSched = inDirectFlightSchedMaps.get(flightSched.getFlightScheduleId());
                if (nextFlightSched != null) {
                    flightSchedHelper.setNextFlightSched(nextFlightSched);
                }
                flightSchedHelpers.add(flightSchedHelper);
            }
            if (selecteRoute) {
                return crmExNavController.redirectToSearchFlightStatus() + "?i=0";
            } else {
                return crmExNavController.redirectToSearchFlightStatus() + "?i=1";
            }
        } catch (NoSuchFlightSchedulException e) {
            msgController.addErrorMessage(e.getMessage());
        }
        return "";
    }

    //
    //Getter and Setter
    //
    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getScheduleChoice() {
        return scheduleChoice;
    }

    public void setScheduleChoice(String scheduleChoice) {
        this.scheduleChoice = scheduleChoice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Airport getDeptAirport() {
        return deptAirport;
    }

    public void setDeptAirport(Airport deptAirport) {
        this.deptAirport = deptAirport;
    }

    public Airport getArrAirport() {
        return arrAirport;
    }

    public void setArrAirport(Airport arrAirport) {
        this.arrAirport = arrAirport;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public boolean isSelecteRoute() {
        return selecteRoute;
    }

    public void setSelecteRoute(boolean selecteRoute) {
        this.selecteRoute = selecteRoute;
    }

    public List<FlightSchedule> getFlightScheds() {
        return flightScheds;
    }

    public void setFlightScheds(List<FlightSchedule> flightScheds) {
        this.flightScheds = flightScheds;
    }

    public Map<Long, FlightSchedule> getInDirectFlightSchedMaps() {
        return inDirectFlightSchedMaps;
    }

    public void setInDirectFlightSchedMaps(Map<Long, FlightSchedule> inDirectFlightSchedMaps) {
        this.inDirectFlightSchedMaps = inDirectFlightSchedMaps;
    }
}
