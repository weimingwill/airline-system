/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.crm.session.BookingSessionLocal;
import ams.crm.util.helper.SearchFlightHelper;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;

/**
 *
 * @author weiming
 */
@Named(value = "bookingManager")
@SessionScoped
public class BookingManager implements Serializable {

    @Inject
    CrmExNavController crmExNavController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private BookingSessionLocal bookingSession;
    
    //Search conditions
    private SearchFlightHelper searchFlightHelper = new SearchFlightHelper();
    
    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private Airport deptAirport = new Airport();
    private Airport arrAirport = new Airport();
    private List<Airport> allAirports;
    private Date deptDate;
    private Date arrDate;
    private boolean showPremium;
    private String promoCode;
    private String choice;
    
    //Search for flights
    private List<FlightSchedule> directFlightScheds;
    private List<FlightSchedule> inDirectFlightScheds;
    
    
    /**
     * Creates a new instance of bookingManager
     */
    @PostConstruct
    public void init() {
        allAirports = routePlanningSession.getAllAirports();
        initialDate();
    }

    public BookingManager() {
    }

    private void initialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        searchFlightHelper.setDeptDate(calendar.getTime());
        deptDate = calendar.getTime();
        calendar.add(Calendar.DATE, 3);
        searchFlightHelper.setArrDate(calendar.getTime());
        arrDate = calendar.getTime();
    }

    public void onDeptDateChange() {
        arrDate = deptDate;
    }

    //Auto complete departure airport when typing in.
    public List<Airport> completeDeptAirport(String query) {
        List<Airport> deptAirports = allAirports;

        deptAirports.remove(searchFlightHelper.getArrAirport());
        query = query.toLowerCase();
        return completeAirport(deptAirports, query);
    }

    //Auto complete arrive airport when typing in.
    public List<Airport> completeArrAirport(String query) {
        List<Airport> arrAirports = allAirports;
//        arrAirports.remove(deptAirport);
        System.out.println("Departure airport: " + searchFlightHelper.getDeptAirport());
        arrAirports.remove(searchFlightHelper.getDeptAirport());
        query = query.toLowerCase();
        return completeAirport(arrAirports, query);
    }

    //Auto complete airport.
    public List<Airport> completeAirport(List<Airport> allAirports, String query) {
        List<Airport> filteredAirports = new ArrayList<>();
        Set<Airport> hs = new HashSet<>();

        for (Airport airport : allAirports) {
            System.out.println("Airport: " + airport.getId());
            if (airport.getAirportName().toLowerCase().startsWith(query)
                    || airport.getCity().getCityName().toLowerCase().startsWith(query)
                    || airport.getCountry().getCountryName().toLowerCase().startsWith(query)) {
                hs.add(airport);
            }
        }
        filteredAirports.addAll(hs);
        return filteredAirports;
    }

    public String searchFlight() {
        return crmExNavController.redirectToSearchFlightResult();
    }
    
    public void searchForOneWayFlights() {
        List<List<FlightSchedule>> fligthScheds = bookingSession.searchForOneWayFlights(deptAirport, arrAirport, deptDate, showPremium, adultNo + childrenNo);
        directFlightScheds = fligthScheds.get(0);
        inDirectFlightScheds = fligthScheds.get(1);
    }
    
    
    //
    //Getter and Setter    
    //
    public int getAdultNo() {
        return adultNo;
    }

    public void setAdultNo(int adultNo) {
        this.adultNo = adultNo;
    }

    public int getChildrenNo() {
        return childrenNo;
    }

    public void setChildrenNo(int childrenNo) {
        this.childrenNo = childrenNo;
    }

    public int getInfantNo() {
        return infantNo;
    }

    public void setInfantNo(int infantNo) {
        this.infantNo = infantNo;
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

    public Date getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(Date deptDate) {
        this.deptDate = deptDate;
    }

    public Date getArrDate() {
        return arrDate;
    }

    public void setArrDate(Date arrDate) {
        this.arrDate = arrDate;
    }

    public boolean isShowPremium() {
        return showPremium;
    }

    public void setShowPremium(boolean showPremium) {
        this.showPremium = showPremium;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public List<FlightSchedule> getDirectFlightScheds() {
        return directFlightScheds;
    }

    public void setDirectFlightScheds(List<FlightSchedule> directFlightScheds) {
        this.directFlightScheds = directFlightScheds;
    }

    public SearchFlightHelper getSearchFlightHelper() {
        return searchFlightHelper;
    }

    public void setSearchFlightHelper(SearchFlightHelper searchFlightHelper) {
        this.searchFlightHelper = searchFlightHelper;
    }
    
    
}
