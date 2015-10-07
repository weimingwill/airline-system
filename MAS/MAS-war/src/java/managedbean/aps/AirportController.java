/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Airport;
import ams.aps.entity.City;
import ams.aps.entity.Country;
import ams.aps.session.RoutePlanningSessionLocal;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "airportController")
@ViewScoped
public class AirportController implements Serializable {

    @Inject
    private MsgController msgController;

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    
    private Country country;
    private List<Country> countries;

    private City city;
    private List<City> cities;

    private Airport airport;
    private List<Airport> airports;
    
    private boolean skip;
    
    /**
     * Creates a new instance of HubController
     */
    public AirportController() {
    }
    
    public void onCountryChange() {
        setCities((List<City>) routePlanningSession.getCityListByCountry(getCountry().getIsoCode()));
        setCity(null);
        setAirport(null);
    }

    public void onCityChange() {
        setAirports((List<Airport>) routePlanningSession.getAirportListByCityID(getCity().getId()));
        setAirport(null);
    }

    public void addCountry(ActionEvent event){
        country.setIsoCode(country.getCountryName().toUpperCase());
        if(routePlanningSession.addCountry(country.getIsoCode(), country.getCountryName())){
            msgController.addMessage("Add country succesfully!");
        }else{
            msgController.addErrorMessage("Failed to add country!");
        }
        getCountries();
    }
    
    public void addCity(){
        if(routePlanningSession.addCity(country.getIsoCode(), city.getCityName(), city.getUTC())){
            msgController.addMessage("Add city succesfully!");
        }else{
            msgController.addErrorMessage("Failed to add city!");
        }
        getCities();
    }
    
    public void addAirport(){
        airport.setIataCode(airport.getIataCode().toUpperCase());
        airport.setIcaoCode(airport.getIcaoCode().toUpperCase());
        if (routePlanningSession.addAirport(country.getIsoCode(), city.getCityName(), airport)) {
            msgController.addMessage("Add airport succesfully!");
        }else{
            msgController.addErrorMessage("Failed to add city!");
        }
    }
    
    public String onFlowProcess(FlowEvent event) {
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
    
    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the countries
     */
    public List<Country> getCountries() {
        return (List<Country>) routePlanningSession.getCountryList();
    }

    /**
     * @param countries the countries to set
     */
    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    /**
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return the cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     * @param cities the cities to set
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    /**
     * @return the airport
     */
    public Airport getAirport() {
        return airport;
    }

    /**
     * @param airport the airport to set
     */
    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    /**
     * @return the airports
     */
    public List<Airport> getAirports() {
        return airports;
    }

    /**
     * @param airports the airports to set
     */
    public void setAirports(List<Airport> airports) {
        this.airports = airports;
    }

    /**
     * @return the skip
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * @param skip the skip to set
     */
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
    
}
