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
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.util.helper.FlightSchedBookingClsHelper;
import ams.aps.entity.Airport;
import ams.aps.entity.FlightSchedule;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import ams.ars.entity.Booking;
import ams.crm.entity.Customer;
import ams.crm.entity.helper.Phone;
import ams.crm.session.BookingSessionLocal;
import ams.crm.util.helper.BookingHelper;
import ams.crm.util.helper.ChannelHelper;
import ams.crm.util.helper.FlightSchedHelper;
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
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
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
    @Inject
    NavigationController navigationController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private BookingSessionLocal bookingSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;
    @EJB
    private RevMgmtSessionLocal revMgmtSession;

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
    private String promoCode;
    private String choice;
    boolean arrDateShow;

    //Search Results
    private List<FlightSchedule> flightScheds = new ArrayList<>();
    private List<FlightSchedule> inDirectFlightScheds = new ArrayList<>();
    private List<TicketFamily> tixFams;
    private Map<Long, FlightSchedule> inDirectFlightSchedMaps = new HashMap<>();
    private Map<String, FlightSchedBookingClsHelper> fbHelperMaps = new HashMap<>();
    private Map<String, FlightScheduleBookingClass> fbMaps = new HashMap<>();
    private List<FlightSchedHelper> flightSchedHelpers = new ArrayList<>();

    private String searchDeptDate;
    private String searchArrDate;

    private FlightSchedBookingClsHelper selectedFbHelper;
    private FlightScheduleBookingClass selectedFb = new FlightScheduleBookingClass();
    private List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers = new ArrayList<>();

    //Passenger details
    private BookingHelper bookingHelper;

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

    public String searchFlights() {
        searchDeptDate = DateHelper.convertDateTime(deptDate);
        searchArrDate = DateHelper.convertDateTime(arrDate);

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
        flightSchedHelpers = new ArrayList<>();
        inDirectFlightSchedMaps = new HashMap<>();
        flightScheds = bookingSession.searchForOneWayFlights(deptAirport, arrAirport, deptDate, inDirectFlightSchedMaps);
        getFlightSchedLowestTixFams();
        getAllFlightSchedBookingClsHelpers();

        for (FlightSchedule flightSched : flightScheds) {
            FlightSchedHelper flightSchedHelper = new FlightSchedHelper();
            flightSchedHelper.setFlightSched(flightSched);
            setFbsAndFbHelpers(flightSchedHelper, flightSched);
            FlightSchedule nextFlightSched = inDirectFlightSchedMaps.get(flightSched.getFlightScheduleId());
            if (nextFlightSched != null) {
                flightSchedHelper.setTotalDur(getFlightSchedTotalDur(flightSched, nextFlightSched));
                flightSchedHelper.setNextFlightSched(nextFlightSched);
            } else {
                flightSchedHelper.setTotalDur(getFlightSchedTotalDur(flightSched));
            }

            flightSchedHelpers.add(flightSchedHelper);
        }
    }

    private void setFbsAndFbHelpers(FlightSchedHelper flightSchedHelper, FlightSchedule flightSched) {
        List<FlightSchedBookingClsHelper> fbHelpers = new ArrayList<>();
        List<FlightScheduleBookingClass> fbs = new ArrayList<>();
        Set<FlightSchedBookingClsHelper> fbHelperHs = new HashSet<>();
        for (FlightSchedBookingClsHelper fbHelper : flightSchedBookingClsHelpers) {
            if (Objects.equals(fbHelper.getFlightSchedBookingCls().getFlightSchedule().getFlightScheduleId(), flightSched.getFlightScheduleId())) {
                fbHelperHs.add(fbHelper);
                fbs.add(fbHelper.getFlightSchedBookingCls());
            }
        }
        fbHelpers.addAll(fbHelperHs);
        flightSchedHelper.setFbHelpers(fbHelpers);
        flightSchedHelper.setFlightSchedBookingClses(fbs);
    }

    public void searchForReturnFlights() {
        System.out.println("searchForReturnFlights");
    }

    private String getFlightSchedTotalDur(FlightSchedule flightSched) {
        return DateHelper.convertMSToHourMinute(DateHelper.calcDateDiff(flightSched.getDepartDate(), flightSched.getArrivalDate()));
    }

    private String getFlightSchedTotalDur(FlightSchedule flightSched, FlightSchedule nextFlightSched) {
        return DateHelper.convertMSToHourMinute(DateHelper.calcDateDiff(flightSched.getDepartDate(), nextFlightSched.getArrivalDate()));
    }

    private void getAllFlightSchedBookingClsHelpers() {
        flightSchedBookingClsHelpers = new ArrayList<>();
        fbMaps = new HashMap<>();
        fbHelperMaps = new HashMap<>();
        flightSchedBookingClsHelpers = bookingSession.getAllFlightSchedBookingClses(flightScheds, tixFams, arrAirport, ChannelHelper.ARS, adultNo + childrenNo, fbMaps, fbHelperMaps);
    }

    private void getFlightSchedLowestTixFams() {
        tixFams = bookingSession.getFlightSchedLowestTixFams(flightScheds, selectedCabinCls);
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
        return inDirectFlightSchedMaps.get(flightSchedule.getFlightScheduleId());
    }

    public void onFlightSchedRadioSelected() throws NoSuchFlightSchedulException {
        System.out.println("Selected FB: " + selectedFb);
        selectedFbHelper = fbHelperMaps.get(selectedFb.getFlightScheduleBookingClassId().toString());
        System.out.println("selectedFbHelper: " + selectedFbHelper);
    }

    public String toEnterPassengerDetails() {
        setBookingHelper();
        System.out.println("bookingHelper Adult: " + bookingHelper.getAdults().isEmpty());
        return crmExNavController.redirectToPassengerInfo();
    }

    private void setBookingHelper() {
        bookingHelper = new BookingHelper();
        List<Customer> adults = new ArrayList<>();
        for (int i = 0; i < adultNo; i++) {
            Customer customer = new Customer();
            customer.setIsAdult(true);
            adults.add(customer);
        }
        List<Customer> children = new ArrayList<>();
        for (int i = 0; i < childrenNo; i++) {
            Customer customer = new Customer();
            customer.setIsAdult(false);
            children.add(customer);
        }
        Booking booking = new Booking();
        Phone phone = new Phone();
        booking.setPhoneNo(phone);
        bookingHelper.setBooking(booking);
        bookingHelper.setAdults(adults);
        bookingHelper.setChildren(children);
        bookingHelper.setChannel(ChannelHelper.ARS);
        List<FlightScheduleBookingClass> fbs = new ArrayList<>();
        fbs.add(selectedFb);
        FlightScheduleBookingClass nextFb = fbMaps.get(selectedFb.getFlightScheduleBookingClassId().toString());
        if (nextFb != null) {
            fbs.add(nextFb);
        }
        bookingHelper.setFlightSchedBookingClses(fbs);
    }

    public FlightSchedule getSelectedFlightSched() throws NoSuchFlightSchedulException {
        FlightSchedule f = new FlightSchedule();
        return revMgmtSession.getFlightScheduleById(selectedFb.getFlightSchedule().getFlightScheduleId());
    }

    public String bookingFlight() {
        System.out.println("Booking Helper email: " + bookingHelper.getBooking().getEmail());
        System.out.println("Booking Helper phone No: " + bookingHelper.getBooking().getPhoneNo().getCountryCode() + " " + bookingHelper.getBooking().getPhoneNo().getAreaCode() + " " + bookingHelper.getBooking().getPhoneNo().getNumber());
        System.out.println("Booking Helper customers: " + bookingHelper.getAdults().get(0).getFirstName());
        bookingSession.bookingFlight(bookingHelper);

        msgController.addMessage("Booking flight successfully!");
        return navigationController.redirectToCurrentPage();

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
        return flightScheds;
    }

    public void setFligthScheds(List<FlightSchedule> flightScheds) {
        this.flightScheds = flightScheds;
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

    public FlightScheduleBookingClass getSelectedFb() {
        return selectedFb;
    }

    public void setSelectedFb(FlightScheduleBookingClass selectedFb) {
        this.selectedFb = selectedFb;
    }

    public BookingHelper getBookingHelper() {
        return bookingHelper;
    }

    public void setBookingHelper(BookingHelper bookingHelper) {
        this.bookingHelper = bookingHelper;
    }

    public List<FlightSchedHelper> getFlightSchedHelpers() {
        return flightSchedHelpers;
    }

    public void setFlightSchedHelpers(List<FlightSchedHelper> flightSchedHelpers) {
        this.flightSchedHelpers = flightSchedHelpers;
    }

    public List<TicketFamily> getTixFams() {
        return tixFams;
    }

    public void setTixFams(List<TicketFamily> tixFams) {
        this.tixFams = tixFams;
    }

}
