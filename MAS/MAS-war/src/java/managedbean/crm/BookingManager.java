/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.CabinClass;
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.crm.session.BookingSessionLocal;
import ams.crm.util.helper.ChannelHelper;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.MsgController;
import mas.util.helper.DateHelper;

/**
 *
 * @author weiming
 */
@Named(value = "bookingManager")
@SessionScoped
public class BookingManager implements Serializable {

    @Inject
    CrmExNavController crmExNavController;
    @Inject
    MsgController msgController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private BookingSessionLocal bookingSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;

    //Search conditions
    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private Airport deptAirport = new Airport();
    private Airport arrAirport = new Airport();
    private List<Airport> allAirports;
    private Date deptDate;
    private Date arrDate;
    private List<CabinClass> cabinClses;
    private CabinClass selectedCabinCls;
    private boolean showPremium;
    private String promoCode;
    private String choice;
    boolean arrDateShow;

    //Search Results
    private List<FlightSchedule> fligthScheds = new ArrayList<>();
    private List<FlightSchedule> inDirectFlightScheds = new ArrayList<>();
    private Map<FlightSchedule, FlightSchedule> inDirectFlightSchedMaps = new HashMap<>();
    private Map<FlightScheduleBookingClass, FlightSchedBookingClsHelper> flightSchedBookingClsMaps = new HashMap<>();

    private String searchDeptDate;
    private String searchArrDate;

    private FlightSchedBookingClsHelper selectedFbHelper;
    private FlightScheduleBookingClass selectedFb;

    /**
     * Creates a new instance of bookingManager
     */
    @PostConstruct
    public void init() {
        allAirports = routePlanningSession.getAllAirports();
        cabinClses = productDesignSession.getAllCabinClass();
        deptAirport = routePlanningSession.getAirportByICAOCode("WSSS");
        choice = "return";
        arrDateShow = true;
        initialDate();
    }

    public BookingManager() {
    }

    private void initialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        deptDate = calendar.getTime();
        calendar.add(Calendar.DATE, 3);
        arrDate = calendar.getTime();
    }

    public void onDeptDateChange() {
        arrDate = deptDate;
    }

    public void onChoiceSelectd() {
        if ("oneway".equals(choice)) {
            arrDateShow = false;
        } else if ("return".equals(choice)) {
            arrDateShow = true;
        }
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

    //Auto complete airport.
    public List<Airport> completeAirport(List<Airport> allAirports, String query) {
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

    public void resetDeptDate(ActionEvent event) {
        System.out.println("Departure date: " + deptDate);
        FacesContext context = FacesContext.getCurrentInstance();
        String departureDate = context.getApplication().evaluateExpressionGet(context, "date", String.class);
        deptDate = DateHelper.convertStringToDate(departureDate);
        System.out.println("Departure date: " + deptDate);
    }

    public void onPriceSelected() {
        System.out.println("Selected FB Helper " + selectedFbHelper);
    }

    public String searchFlights() {
        searchDeptDate = DateHelper.convertDateTime(deptDate);
        searchArrDate = DateHelper.convertDateTime(arrDate);
        inDirectFlightSchedMaps = new HashMap<>();
        flightSchedBookingClsMaps = new HashMap<>();
        try {
            if (choice.equals("oneway")) {
                searchForOneWayFlights();
            } else if (choice.equals("return")) {
                searchForReturnFlights();
            }
            return crmExNavController.redirectToSearchFlightResult();
        } catch (NoSuchFlightSchedulException e) {
            msgController.addErrorMessage(e.getMessage());
            return "";
        }
    }

    public void searchForOneWayFlights() throws NoSuchFlightSchedulException {
        System.out.println("searchForOneWayFlights");
        fligthScheds = bookingSession.searchForOneWayFlights(deptAirport, arrAirport, deptDate, inDirectFlightSchedMaps);
    }

    public void searchForReturnFlights() {
        System.out.println("searchForReturnFlights");
    }

    public List<TicketFamily> getFlightSchedLowestTixFams() {
        return bookingSession.getFlightSchedLowestTixFams(fligthScheds, selectedCabinCls);
    }

    public String getFlightSchedTotalDur(FlightSchedule flightSched) {
        return DateHelper.convertMSToHourMinute(DateHelper.calcDateDiff(flightSched.getDepartDate(), flightSched.getArrivalDate()));
    }

    public String getFlightSchedTotalDur(FlightSchedule flightSched, FlightSchedule nextFlightSched) {
        return DateHelper.convertMSToHourMinute(DateHelper.calcDateDiff(flightSched.getDepartDate(), nextFlightSched.getArrivalDate()));
    }

    public List<FlightScheduleBookingClass> getFlightSchedBookingCls(FlightSchedule flightSched) {
        return bookingSession.getOpenedFlightSchedBookingClses(flightSched, getFlightSchedLowestTixFams(), ChannelHelper.ARS, adultNo + childrenNo, flightSchedBookingClsMaps);
    }

    public List<FlightSchedBookingClsHelper> getFlightSchedBookingClsHelpers(FlightSchedule flightSched) {
        List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers = new ArrayList<>();
        for (FlightScheduleBookingClass flightSchedBookingCls : getFlightSchedBookingCls(flightSched)) {
            flightSchedBookingClsHelpers.add(flightSchedBookingClsMaps.get(flightSchedBookingCls));
        }
        return flightSchedBookingClsHelpers;
    }

    public List<String> getPreviousThreeDays() {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        calendar.add(Calendar.DATE, -3);
        dates.add(DateHelper.convertDateTime(calendar.getTime()));
        for (int i = 0; i < 2; i++) {
            calendar.add(Calendar.DATE, 1);
            dates.add(DateHelper.convertDateTime(calendar.getTime()));
        }
        return dates;
    }

    public List<String> getNextThreeDays() {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deptDate);
        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.DATE, 1);
            dates.add(DateHelper.convertDateTime(calendar.getTime()));
        }
        return dates;
    }

    public FlightSchedule getMappedFlightSched(FlightSchedule flightSchedule) {
        return inDirectFlightSchedMaps.get(flightSchedule);
    }

    public void onFlightSchedRadioSelected() {
        System.out.println("Selected FB: " + selectedFb);
        selectedFbHelper = flightSchedBookingClsMaps.get(selectedFb);
        System.out.println("selectedFbHelper: " + selectedFbHelper);
    }

    public String test() {
        System.out.println("Selected FB: " + selectedFb);
        return crmExNavController.redirectToPassengerInfo();
    }

    public String testValue(FlightSchedBookingClsHelper fbHelper) {
        return String.valueOf(fbHelper.getFlightSchedBookingCls().getFlightScheduleBookingClassId());
    }

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

    public List<FlightSchedule> getFligthScheds() {
        return fligthScheds;
    }

    public void setFligthScheds(List<FlightSchedule> fligthScheds) {
        this.fligthScheds = fligthScheds;
    }

    public String getSearchDeptDate() {
        return searchDeptDate;
    }

    public void setSearchDeptDate(String searchDeptDate) {
        this.searchDeptDate = searchDeptDate;
    }

    public String getSearchArrDate() {
        return searchArrDate;
    }

    public void setSearchArrDate(String searchArrDate) {
        this.searchArrDate = searchArrDate;
    }

    public List<CabinClass> getCabinClses() {
        return cabinClses;
    }

    public void setCabinClses(List<CabinClass> cabinClses) {
        this.cabinClses = cabinClses;
    }

    public CabinClass getSelectedCabinCls() {
        return selectedCabinCls;
    }

    public void setSelectedCabinCls(CabinClass selectedCabinCls) {
        this.selectedCabinCls = selectedCabinCls;
    }

    public boolean isArrDateShow() {
        return arrDateShow;
    }

    public void setArrDateShow(boolean arrDateShow) {
        this.arrDateShow = arrDateShow;
    }

    public FlightSchedBookingClsHelper getSelectedFbHelper() {
        return selectedFbHelper;
    }

    public void setSelectedFbHelper(FlightSchedBookingClsHelper selectedFbHelper) {
        this.selectedFbHelper = selectedFbHelper;
    }

    public List<FlightSchedule> getInDirectFlightScheds() {
        return inDirectFlightScheds;
    }

    public void setInDirectFlightScheds(List<FlightSchedule> inDirectFlightScheds) {
        this.inDirectFlightScheds = inDirectFlightScheds;
    }

    public Map<FlightSchedule, FlightSchedule> getInDirectFlightSchedMaps() {
        return inDirectFlightSchedMaps;
    }

    public void setInDirectFlightSchedMaps(Map<FlightSchedule, FlightSchedule> inDirectFlightSchedMaps) {
        this.inDirectFlightSchedMaps = inDirectFlightSchedMaps;
    }

    public FlightScheduleBookingClass getSelectedFb() {
        return selectedFb;
    }

    public void setSelectedFb(FlightScheduleBookingClass selectedFb) {
        this.selectedFb = selectedFb;
    }
}
