/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ars_crm;

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
import ams.aps.util.helper.FlightSchedStatus;
import ams.ars.entity.AddOn;
import ams.ars.entity.AirTicket;
import ams.ars.entity.Booking;
import ams.ars.entity.PricingItem;
import ams.ars.util.helper.AddOnHelper;
import ams.crm.entity.Customer;
import ams.crm.entity.RegCust;
import ams.crm.entity.helper.Phone;
import ams.crm.session.BookingSessionLocal;
import ams.crm.session.CustomerExSessionLocal;
import ams.crm.util.exception.InvalidPromoCodeException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.BookingHelper;
import ams.crm.util.helper.ChannelHelper;
import ams.crm.util.helper.CustomerHelper;
import ams.crm.util.helper.FlightSchedHelper;
import ams.dcs.entity.Luggage;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import managedbean.application.CrmExNavController;
import managedbean.application.CrmMobileNavController;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import managedbean.crm.CustomerLoginManager;
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
    @Inject
    BookingBacking bookingBacking;
    @Inject
    CustomerLoginManager customerLoginManager;
    @Inject
    CrmMobileNavController crmMobileNavController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private BookingSessionLocal bookingSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;
    @EJB
    private RevMgmtSessionLocal revMgmtSession;
    @EJB
    private CustomerExSessionLocal customerExSession;

    //Source: web or mobile
    private List<String> sources;

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
    private List<TicketFamily> tixFams;
    private Map<Long, FlightSchedule> inDirectFlightSchedMaps = new HashMap<>();
    private Map<String, FlightSchedBookingClsHelper> fbHelperMaps = new HashMap<>();
    private Map<String, FlightScheduleBookingClass> fbMaps = new HashMap<>();
    private List<FlightSchedHelper> flightSchedHelpers = new ArrayList<>();

    private String searchDeptDate;
    private String searchArrDate;

    private FlightSchedBookingClsHelper selectedFbHelper;
    private FlightScheduleBookingClass selectedFb;
    private List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers = new ArrayList<>();
    //Passenger details
    private BookingHelper bookingHelper;

    //AddOn
    private List<String> selectedMeals;
    private Map<String, Integer> selectedMealMap = new HashMap<>();
    private List<Double> selectedLuggages;
    private Map<Double, Integer> selectedLuggageMap = new HashMap<>();
    private int selectedNumOfInsurance = 0;
    private double selectedIsurancePrice;
    private double insurancePrice;
    private double luggagePrice;

    //Itinerary
    private Booking booking;
    private List<CustomerHelper> custHelpers;
    private double farePrice;

    //Manage booking
    private boolean isChangeAddOn;
    private List<Double> originLuggages;
    private int originNumOfInsurance = 0;
    //Change addon
    private List<Double> additionalLuggages;
    private double additionalPrice;

    //For mobile
    private FlightSchedHelper selectedFlightSchedHelper;

    /**
     * Creates a new instance of bookingManager
     */
    @PostConstruct
    public void init() {
        sources = new ArrayList<>();
        sources.add("web");
        sources.add("mobile");
        allAirports = routePlanningSession.getAllAirports();
        cabinClses = productDesignSession.getAllCabinClass();
        deptAirport = routePlanningSession.getAirportByICAOCode("WSSS");
        choice = "return";
        arrDateShow = true;
        insurancePrice = bookingSession.getTravelInsurance().getPrice();
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

    public String searchFlights(String method, String channel) {
        selectedFbHelper = null;
        searchDeptDate = DateHelper.convertDateTime(deptDate);
        searchArrDate = DateHelper.convertDateTime(arrDate);
        try {
            RegCust regCust = getRegCustIfLoggined();
            bookingSession.verifyPromoCodeUsability(promoCode, regCust);
            if (choice.equals("oneway")) {
                searchForOneWayFlights(channel);
            } else if (choice.equals("return")) {
                searchForOneWayFlights(channel);
//                searchForReturnFlights();
            }
            if (method.equals(sources.get(0))) {
                return crmExNavController.redirectToSearchFlightResult();
            } else {
                return crmMobileNavController.redirectToSearchFlightResult();
            }
        } catch (NoSuchFlightSchedulException | InvalidPromoCodeException e) {
            msgController.addErrorMessage(e.getMessage());
            return "";
        }
    }

    private RegCust getRegCustIfLoggined() {
        RegCust regCust = null;
        if (customerLoginManager.isLoggedIn()) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            String email = (String) sessionMap.get("email");
            try {
                regCust = customerExSession.getRegCustByEmail(email);
            } catch (NoSuchRegCustException ex) {
            }
        }
        return regCust;
    }

    public void searchForOneWayFlights(String channel) throws NoSuchFlightSchedulException {
        System.out.println("searchForOneWayFlights");
        flightSchedHelpers = new ArrayList<>();
        inDirectFlightSchedMaps = new HashMap<>();
        flightScheds = bookingSession.searchForOneWayFlights(deptAirport, arrAirport, deptDate, inDirectFlightSchedMaps, FlightSchedStatus.METHOD_FLIGHT_STATUS, channel);
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

    public String getFlightSchedTotalDur(FlightSchedule flightSched) {
        if (flightSched != null && flightSched.getDepartDate() != null && flightSched.getArrivalDate() != null) {
            return DateHelper.convertMSToHourMinute(DateHelper.calcDateDiff(flightSched.getDepartDate(), flightSched.getArrivalDate()));
        } else {
            return "";
        }
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
        if (flightSchedule != null) {
            return inDirectFlightSchedMaps.get(flightSchedule.getFlightScheduleId());
        } else {
            return new FlightSchedule();
        }
    }

    public void onFlightSchedRadioSelected() throws NoSuchFlightSchedulException {
        selectedFbHelper = fbHelperMaps.get(selectedFb.getFlightScheduleBookingClassId().toString());
    }

    public FlightSchedBookingClsHelper getFbHelperByFb(FlightScheduleBookingClass fb) {
        return fbHelperMaps.get(fb.getFlightScheduleBookingClassId().toString());
    }

    public String toEnterPassengerDetails() {
        setBookingHelper();
        return crmExNavController.redirectToPassengerInfo();
    }

    public String toEnterPassengerDetails(FlightSchedBookingClsHelper fbHelper) throws NoSuchFlightSchedulException {
        selectedFbHelper = fbHelper;
        selectedFb = selectedFbHelper.getFlightSchedBookingCls();
        setBookingHelper();
        return crmMobileNavController.redirectToPassengerInfo();
    }

    private void setBookingHelper() {
        bookingHelper = new BookingHelper();
        List<CustomerHelper> adults = new ArrayList<>();
        Booking booking = new Booking();
        Phone phone = new Phone();
        int newAdultNo = adultNo;
        //If customer is loggedIn, put his/her personal information to passenger detail
        RegCust regCust = getRegCustIfLoggined();
        if (regCust != null) {
            CustomerHelper customerHelper = new CustomerHelper();
            Customer customer = setRegCustToCustomer(regCust);
            customerHelper.setCustomer(customer);
            customerHelper.setRegCust(regCust);
            adults.add(customerHelper);
            booking.setEmail(regCust.getEmail());
            phone = regCust.getPhone();
            newAdultNo--;
        }

        for (int i = 0; i < newAdultNo; i++) {
            CustomerHelper customerHelper = new CustomerHelper();
            Customer customer = new Customer();
            customer.setIsAdult(true);
            customerHelper.setCustomer(new Customer());
            adults.add(customerHelper);
        }
        List<CustomerHelper> children = new ArrayList<>();
        for (int i = 0; i < childrenNo; i++) {
            CustomerHelper customerHelper = new CustomerHelper();
            Customer customer = new Customer();
            customer.setIsAdult(true);
            customerHelper.setCustomer(new Customer());
            children.add(customerHelper);
        }

        booking.setPhoneNo(phone);
        bookingHelper.setBooking(booking);
        bookingHelper.setAdults(adults);
        bookingHelper.setChildren(children);
        List<CustomerHelper> customerHelpers = new ArrayList<>();
        customerHelpers.addAll(adults);
        customerHelpers.addAll(children);
        bookingHelper.setCustomers(customerHelpers);
        bookingHelper.setChannel(ChannelHelper.ARS);
        List<FlightScheduleBookingClass> fbs = new ArrayList<>();
        fbs.add(selectedFb);
        farePrice = selectedFb.getPrice();
        FlightScheduleBookingClass nextFb = fbMaps.get(selectedFb.getFlightScheduleBookingClassId().toString());
        if (nextFb != null) {
            fbs.add(nextFb);
            farePrice += nextFb.getPrice();
        }
        bookingHelper.setFlightSchedBookingClses(fbs);
        //PromoCode = "" if nothing entered
        bookingHelper.setPromoCode(promoCode);
        setBookingHelperTotalPrice();
    }

    private Customer setRegCustToCustomer(RegCust regCust) {
        Customer customer = new Customer();
        customer.setTitle(regCust.getTitle());
        customer.setFirstName(regCust.getFirstName());
        customer.setLastName(regCust.getLastName());
        customer.setGender(regCust.getGender());
        customer.setIsAdult(true);
        customer.setNationality(regCust.getNationality());
        customer.setPassportNo(regCust.getPassportNo());
        customer.setPassportExpDate(regCust.getPassportExpDate());
        customer.setPassportIssueDate(regCust.getPassportIssueDate());
        customer.setDob(regCust.getDob());
        return customer;
    }

    public FlightSchedule getSelectedFlightSched() throws NoSuchFlightSchedulException {
//        if (selectedFb.getFlightSchedule() == null) {
//            return new FlightSchedule();
//        }
        return revMgmtSession.getFlightScheduleById(selectedFb.getFlightSchedule().getFlightScheduleId());
    }

    //Selected AddOn
    public void onMealSelected() {
        selectedMeals = new ArrayList<>();
        for (CustomerHelper customerHelper : bookingHelper.getCustomers()) {
            if (customerHelper.getMeal() != null) {
                String meal = customerHelper.getMeal().getDescription();
                if (selectedMeals.contains(meal)) {
                    selectedMealMap.put(meal, selectedMealMap.get(meal) + 1);
                } else {
                    selectedMeals.add(meal);
                    selectedMealMap.put(meal, 1);
                }
            }
        }
    }

    public void onLuggageSelected() {
        luggagePrice = 0;
        selectedLuggages = new ArrayList<>();
        for (CustomerHelper customerHelper : bookingHelper.getCustomers()) {
            if (customerHelper.getLuggage() != null) {
                Double luggageWeight = customerHelper.getLuggage().getMaxWeight();
                if (selectedLuggages.contains(luggageWeight)) {
                    selectedLuggageMap.put(luggageWeight, selectedLuggageMap.get(luggageWeight) + 1);
                } else {
                    selectedLuggages.add(luggageWeight);
                    selectedLuggageMap.put(luggageWeight, 1);
                }
                luggagePrice += bookingBacking.getLuggageWeightPriceMap().get(luggageWeight);
            }
        }
        setBookingHelperTotalPrice();
    }

    public void onInsuranceSelected() {
        selectedNumOfInsurance = 0;
        for (CustomerHelper customerHelper : bookingHelper.getCustomers()) {
            if (customerHelper.isInsurance()) {
                selectedNumOfInsurance++;
            }
        }
        selectedIsurancePrice = selectedNumOfInsurance * insurancePrice;
        setBookingHelperTotalPrice();
    }

    private void setBookingHelperTotalPrice() {
        double price = 0;
        for (PricingItem pricingItem : bookingBacking.getPricingItems()) {
            price += farePrice * pricingItem.getPrice();
        }

        for (FlightScheduleBookingClass fb : bookingHelper.getFlightSchedBookingClses()) {
            price += fb.getPrice() * (adultNo + childrenNo);
        }
        price = price + luggagePrice + selectedIsurancePrice;
        bookingHelper.setTotalPrice(price);
    }

    public String bookingFlight(String method) {
        try {
            booking = bookingSession.bookingFlight(bookingHelper);
            custHelpers = setCustomerHelpers(booking);
            farePrice = setFarePrice(booking);
            msgController.addMessage("Booking flight successfully!");
            clearVariables();
            if (sources.get(0).equals(method)) {
                return crmExNavController.redirectToItinerary();
            } else {
                return crmMobileNavController.redirectToItinerary();
            }
        } catch (InvalidPromoCodeException e) {
            msgController.addErrorMessage(e.getMessage());
            return "";
        }
    }

    private void clearVariables() {
        adultNo = 1;
        childrenNo = 0;
        infantNo = 0;
        deptAirport = routePlanningSession.getAirportByICAOCode("WSSS");
        arrAirport = new Airport();
        initialDate();
        selectedCabinCls = null;
        promoCode = "";
        flightScheds = new ArrayList<>();
        tixFams = new ArrayList<>();
        inDirectFlightSchedMaps = new HashMap<>();
        fbHelperMaps = new HashMap<>();
        fbMaps = new HashMap<>();
        flightSchedHelpers = new ArrayList<>();
        selectedFbHelper = null;
        selectedFb = null;
        flightSchedBookingClsHelpers = new ArrayList<>();
        bookingHelper = null;
        selectedMeals = null;
        selectedMealMap = new HashMap<>();
        selectedLuggages = null;
        selectedLuggageMap = new HashMap<>();
        selectedNumOfInsurance = 0;
        selectedIsurancePrice = 0;
        insurancePrice = insurancePrice = bookingSession.getTravelInsurance().getPrice();
        luggagePrice = 0;
        choice = "return";
        arrDateShow = true;
    }

    public double setFarePrice(Booking booking) {
        double price = 0;
        for (AirTicket airTicket : booking.getAirTickets()) {
            price += airTicket.getFlightSchedBookingClass().getPrice();
        }
        return price;
    }

    public List<CustomerHelper> setCustomerHelpers(Booking booking) {
        List<CustomerHelper> customerHelpers = new ArrayList<>();
        for (AirTicket airTicket : booking.getAirTickets()) {
            Customer customer = airTicket.getCustomer();
            CustomerHelper custHelper = new CustomerHelper();
            boolean exist = false;
            for (CustomerHelper customerHelper : customerHelpers) {
                if (customer.getPassportNo().equals(customerHelper.getCustomer().getPassportNo())) {
                    exist = true;
                }
            }
            if (!exist) {
                custHelper.setCustomer(customer);
                for (AddOn addOn : airTicket.getAddOns()) {
                    if (addOn.getName().equals(AddOnHelper.MEAL)) {
                        custHelper.setMeal(addOn);
                    } else if (addOn.getName().equals(AddOnHelper.INSURANCE)) {
                        if (addOn.getPrice() > 0) {
                            custHelper.setInsurance(true);
                        } else {
                            custHelper.setInsurance(false);
                        }
                    }
                }
                custHelper.setLuggage(airTicket.getPurchasedLuggage());
                customerHelpers.add(custHelper);
            }
        }
        return customerHelpers;
    }

    public List<FlightScheduleBookingClass> getBookingFlightSchedBookingCls(Booking booking) {
        return bookingSession.getBookingFlightSchedBookingClses(booking);
    }

    //Manage booking
    public void toChangeAddOn() {
        isChangeAddOn = true;
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        booking = (Booking) sessionMap.get("booking");
        initializeManageBooking();
    }

    public void initializeManageBooking() {
        adultNo = 0;
        childrenNo = 0;
        deptAirport = booking.getAirTickets().get(0).getFlightSchedBookingClass().getFlightSchedule().getLeg().getDepartAirport();
        deptDate = booking.getAirTickets().get(0).getFlightSchedBookingClass().getFlightSchedule().getDepartDate();
        selectedFb = booking.getAirTickets().get(0).getFlightSchedBookingClass();
        selectedCabinCls = selectedFb.getBookingClass().getTicketFamily().getCabinClass();
        arrDate = deptDate;
        arrAirport = deptAirport;
        for (AirTicket airTicket : booking.getAirTickets()) {
            if (arrDate.before(airTicket.getFlightSchedBookingClass().getFlightSchedule().getArrivalDate())) {
                arrDate = airTicket.getFlightSchedBookingClass().getFlightSchedule().getArrivalDate();
                arrAirport = airTicket.getFlightSchedBookingClass().getFlightSchedule().getLeg().getArrivalAirport();
            }
        }
        try {
            searchForOneWayFlights(selectedFb.getBookingClass().getChannel());
            onFlightSchedRadioSelected();
        } catch (Exception e) {
        }
        setBookingNumOfCustomer();
        initializeBookingHelper();
        onMealSelected();
        onLuggageSelected();
        onInsuranceSelected();
        originLuggages = getAllLuggages();
        originNumOfInsurance = selectedNumOfInsurance;
    }

    private void setBookingNumOfCustomer() {
        List<String> custPassports = new ArrayList<>();
        adultNo = 0;
        childrenNo = 0;
        for (AirTicket airTicket : booking.getAirTickets()) {
            if (!custPassports.contains(airTicket.getCustomer().getPassportNo())) {
                if (airTicket.getCustomer().getIsAdult()) {
                    adultNo++;
                } else {
                    childrenNo++;
                }
                custPassports.add(airTicket.getCustomer().getPassportNo());
            }
        }
    }

    private void initializeBookingHelper() {
        bookingHelper = new BookingHelper();
        List<CustomerHelper> adults = new ArrayList<>();
        List<CustomerHelper> children = new ArrayList<>();
        List<CustomerHelper> customerHelpers = new ArrayList<>();
        List<String> custPassports = new ArrayList<>();
        for (AirTicket airTicket : booking.getAirTickets()) {
            if (!custPassports.contains(airTicket.getCustomer().getPassportNo())) {
                CustomerHelper customerHelper = new CustomerHelper();
                for (AddOn addOn : airTicket.getAddOns()) {
                    if (AddOnHelper.MEAL.equals(addOn.getName())) {
                        customerHelper.setMeal(addOn);
                    } else if (AddOnHelper.INSURANCE.equals(addOn.getName())) {
                        customerHelper.setInsurance(true);
                    }
                }
                customerHelper.setLuggage(airTicket.getPurchasedLuggage());
                if (airTicket.getCustomer().getIsAdult()) {
                    customerHelper.setCustomer(airTicket.getCustomer());
                    adults.add(customerHelper);
                } else {
                    customerHelper.setCustomer(airTicket.getCustomer());
                    children.add(customerHelper);
                }
                custPassports.add(airTicket.getCustomer().getPassportNo());
            }
        }
        bookingHelper.setBooking(booking);
        bookingHelper.setAdults(adults);
        bookingHelper.setChildren(children);
        customerHelpers.addAll(adults);
        customerHelpers.addAll(children);
        bookingHelper.setCustomers(customerHelpers);
        bookingHelper.setChannel(ChannelHelper.ARS);
        List<FlightScheduleBookingClass> fbs = new ArrayList<>();
        fbs.add(selectedFb);
        FlightScheduleBookingClass nextFb = fbMaps.get(selectedFb.getFlightScheduleBookingClassId().toString());
        if (nextFb != null) {
            fbs.add(nextFb);
        }
        bookingHelper.setFlightSchedBookingClses(fbs);
        setBookingHelperTotalPrice();
    }

    public String toChangeAddOnBookingSummary() {
        List<Double> temp = getAllLuggages();
        additionalLuggages = getAllLuggages();
        System.out.println("Additional Luggage: " + additionalLuggages.get(0));
        additionalPrice = 0;
        for (Double luggage : temp) {
            System.out.println("Temp Luggage: " + luggage);
            if (originLuggages.contains(luggage)) {
                additionalLuggages.remove(luggage);
                originLuggages.remove(luggage);
            }
        }
        for (Double luggage : additionalLuggages) {
            additionalPrice += bookingBacking.getLuggageWeightPriceMap().get(luggage);
        }
        System.out.println("Additional Price: " + additionalPrice);
        for (Double luggage : originLuggages) {
            additionalPrice -= bookingBacking.getLuggageWeightPriceMap().get(luggage);
        }
        System.out.println("Additional Price: " + additionalPrice);
        additionalPrice += (selectedNumOfInsurance - originNumOfInsurance) * insurancePrice;
        return crmExNavController.redirectToBookingSummary();
    }

    //Get luggages with number
    public List<Double> getAllLuggages() {
        List<Double> allLuggages = new ArrayList<>();
        for (Double luggage : selectedLuggages) {
            for (int i = 0; i < selectedLuggageMap.get(luggage); i++) {
                allLuggages.add(luggage);
            }
        }
        return allLuggages;
    }

    public String updateAddOn() {
        booking = bookingSession.updateAddOn(bookingHelper);
        custHelpers = setCustomerHelpers(booking);
        farePrice = setFarePrice(booking);
        msgController.addMessage("Update add on successfully!");
        return crmExNavController.redirectToItinerary();
    }

    public double getLowestPrice(FlightSchedHelper flightSchedHelper) {
        double min = 1000000000;
        for (FlightSchedBookingClsHelper fbHelper : flightSchedHelper.getFbHelpers()) {
            if (min > fbHelper.getPrice()) {
                min = fbHelper.getPrice();
            }
        }
        return min;
    }

    public String toTixFamSelection(FlightSchedHelper flightSchedHelper) {
        selectedFlightSchedHelper = flightSchedHelper;
        return crmMobileNavController.redirectToTixFamSelection();
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

    public List<String> getSelectedMeals() {
        return selectedMeals;
    }

    public void setSelectedMeals(List<String> selectedMeals) {
        this.selectedMeals = selectedMeals;
    }

    public Map<String, Integer> getSelectedMealMap() {
        return selectedMealMap;
    }

    public void setSelectedMealMap(Map<String, Integer> selectedMealMap) {
        this.selectedMealMap = selectedMealMap;
    }

    public List<Double> getSelectedLuggages() {
        return selectedLuggages;
    }

    public void setSelectedLuggages(List<Double> selectedLuggages) {
        this.selectedLuggages = selectedLuggages;
    }

    public Map<Double, Integer> getSelectedLuggageMap() {
        return selectedLuggageMap;
    }

    public void setSelectedLuggageMap(Map<Double, Integer> selectedLuggageMap) {
        this.selectedLuggageMap = selectedLuggageMap;
    }

    public int getSelectedNumOfInsurance() {
        return selectedNumOfInsurance;
    }

    public void setSelectedNumOfInsurance(int selectedNumOfInsurance) {
        this.selectedNumOfInsurance = selectedNumOfInsurance;
    }

    public double getLuggagePrice() {
        return luggagePrice;
    }

    public void setLuggagePrice(double luggagePrice) {
        this.luggagePrice = luggagePrice;
    }

    public double getSelectedIsurancePrice() {
        return selectedIsurancePrice;
    }

    public void setSelectedIsurancePrice(double selectedIsurancePrice) {
        this.selectedIsurancePrice = selectedIsurancePrice;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<CustomerHelper> getCustHelpers() {
        return custHelpers;
    }

    public void setCustHelpers(List<CustomerHelper> custHelpers) {
        this.custHelpers = custHelpers;
    }

    public double getFarePrice() {
        return farePrice;
    }

    public void setFarePrice(double farePrice) {
        this.farePrice = farePrice;
    }

    public boolean isIsChangeAddOn() {
        return isChangeAddOn;
    }

    public void setIsChangeAddOn(boolean isChangeAddOn) {
        this.isChangeAddOn = isChangeAddOn;
    }

    public List<Double> getAdditionalLuggages() {
        return additionalLuggages;
    }

    public void setAdditionalLuggages(List<Double> additionalLuggages) {
        this.additionalLuggages = additionalLuggages;
    }

    public double getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public List<Double> getOriginLuggages() {
        return originLuggages;
    }

    public void setOriginLuggages(List<Double> originLuggages) {
        this.originLuggages = originLuggages;
    }

    public int getOriginNumOfInsurance() {
        return originNumOfInsurance;
    }

    public void setOriginNumOfInsurance(int originNumOfInsurance) {
        this.originNumOfInsurance = originNumOfInsurance;
    }

    public List<Airport> getAllAirports() {
        return allAirports;
    }

    public void setAllAirports(List<Airport> allAirports) {
        this.allAirports = allAirports;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<FlightSchedBookingClsHelper> getFlightSchedBookingClsHelpers() {
        return flightSchedBookingClsHelpers;
    }

    public void setFlightSchedBookingClsHelpers(List<FlightSchedBookingClsHelper> flightSchedBookingClsHelpers) {
        this.flightSchedBookingClsHelpers = flightSchedBookingClsHelpers;
    }

    public FlightSchedHelper getSelectedFlightSchedHelper() {
        return selectedFlightSchedHelper;
    }

    public void setSelectedFlightSchedHelper(FlightSchedHelper selectedFlightSchedHelper) {
        this.selectedFlightSchedHelper = selectedFlightSchedHelper;
    }

}
