/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.FlightScheduleBookingClass;
import ams.aps.entity.FlightSchedule;
import ams.ars.entity.AddOn;
import ams.ars.entity.PricingItem;
import ams.crm.session.BookingSessionLocal;
import ams.dcs.entity.Luggage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author weiming
 */
@Named(value = "bookingBacking")
@ViewScoped
public class BookingBacking implements Serializable {

    @EJB
    private BookingSessionLocal bookingSession;

    private FlightSchedule flightSchedule;
    private FlightScheduleBookingClass selectedFlightSchedBookignCls;

    //Passenger details
    private List<String> titles = new ArrayList<>();
    private List<String> genders = new ArrayList<>();

    //Add On
    private List<AddOn> meals = new ArrayList<>();
    private List<Luggage> luggages = new ArrayList<>();
    private Map<Double,Double> luggageWeightPriceMap = new HashMap<>();

    private List<PricingItem> pricingItems = new ArrayList<>();
    private AddOn travelInsurance;

    /**
     * Creates a new instance of BookingBacking
     */
    @PostConstruct
    public void init() {
        setTitleList();
        setGenders();
        setAddOns();
        setPricingItems();
    }

    public BookingBacking() {
    }

    private void setTitleList() {
        titles.add("Ms");
        titles.add("Mrs");
        titles.add("Mr");
        titles.add("Ms Dr.");
        titles.add("Ms Prof.");
        titles.add("Mr Dr.");
        titles.add("Mr Prof.");
    }

    private void setGenders() {
        genders.add("Male");
        genders.add("Female");
    }

    private void setAddOns() {
        meals = bookingSession.getMeals();
        luggages = bookingSession.getLuggages();
        for (Luggage luggage : luggages) {
            luggageWeightPriceMap.put(luggage.getMaxWeight(), luggage.getPrice());
        }
        travelInsurance = bookingSession.getTravelInsurance();
    }

    private void setPricingItems() {
        pricingItems = bookingSession.getPricingItems();
    }

    
    //
    //Getter nad Setter
    //
    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public FlightScheduleBookingClass getSelectedFlightSchedBookignCls() {
        return selectedFlightSchedBookignCls;
    }

    public void setSelectedFlightSchedBookignCls(FlightScheduleBookingClass selectedFlightSchedBookignCls) {
        this.selectedFlightSchedBookignCls = selectedFlightSchedBookignCls;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }

    public List<AddOn> getMeals() {
        return meals;
    }

    public void setMeals(List<AddOn> meals) {
        this.meals = meals;
    }

    public List<PricingItem> getPricingItems() {
        return pricingItems;
    }

    public void setPricingItems(List<PricingItem> pricingItems) {
        this.pricingItems = pricingItems;
    }

    public List<Luggage> getLuggages() {
        return luggages;
    }

    public void setLuggages(List<Luggage> luggages) {
        this.luggages = luggages;
    }

    public AddOn getTravelInsurance() {
        return travelInsurance;
    }

    public void setTravelInsurance(AddOn travelInsurance) {
        this.travelInsurance = travelInsurance;
    }

    public Map<Double, Double> getLuggageWeightPriceMap() {
        return luggageWeightPriceMap;
    }

    public void setLuggageWeightPriceMap(Map<Double, Double> luggageWeightPriceMap) {
        this.luggageWeightPriceMap = luggageWeightPriceMap;
    }
    
}
