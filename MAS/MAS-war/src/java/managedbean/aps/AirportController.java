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

    private Country country = new Country();
    private Country newCountry = new Country();
    private List<Country> countries;

    private City city = new City();
    private City newCity = new City();
    private List<City> cities;

    private Airport airport = new Airport();
    private List<Airport> airports;

    private boolean skip;

    /**
     * Creates a new instance of HubController
     */
    public AirportController() {
    }

    public void onCountryChange() {
        setCities((List<City>) routePlanningSession.getCityListByCountry(getCountry().getIsoCode()));
        city = new City();
        airport = new Airport();
    }

    public void onCityChange() {
        setAirports((List<Airport>) routePlanningSession.getAirportListByCityID(getCity().getId()));
        airport = new Airport();
    }

    public void addCountry(ActionEvent event) {
        String isoCode = newCountry.getIsoCode();
        String countryName = newCountry.getCountryName();

        newCountry.setIsoCode(isoCode.toUpperCase());
        newCountry.setCountryName(capitalize(countryName));
        if (routePlanningSession.checkISO(newCountry.getIsoCode(), newCountry.getCountryName())) {
            if (routePlanningSession.addCountry(newCountry.getIsoCode(), newCountry.getCountryName())) {
                msgController.addMessage("Add country succesfully!");
                country = newCountry;
                newCountry = new Country();
                getCountries();
            } else {
                msgController.addErrorMessage("Failed to add country!");
            }
        } else {
            msgController.addErrorMessage("ISO Code/ Country name in used already!");
        }
    }

    public void addCity() {
        String cityName = newCity.getCityName();
        newCity.setCityName(capitalize(cityName));
        if (routePlanningSession.checkCityName(newCity.getCityName(), country.getIsoCode())) {
            if (routePlanningSession.addCity(country.getIsoCode(), newCity.getCityName(), newCity.getUTC())) {
                msgController.addMessage("Add city succesfully!");
                city = newCity;
                newCity = new City();
                getCities();
            } else {
                msgController.addErrorMessage("Failed to add city!");
            }
        } else {
            msgController.addErrorMessage("City name exists!");
        }
    }

    public void addAirport() {
        System.out.println("AirportController: " + country.getCountryName() + ", " + city.getCityName());
        System.out.println("Airport: " + airport);
        airport.setIcaoCode(airport.getIcaoCode().toUpperCase());
        airport.setIataCode(airport.getIataCode().toUpperCase());
        airport.setAirportName(capitalize(airport.getAirportName()));

        if (routePlanningSession.checkIATA(airport.getIataCode())) {
            if (routePlanningSession.checkICAO(airport.getIcaoCode())) {
                if (routePlanningSession.addAirport(country.getIsoCode(), city.getCityName(), airport)) {
                    msgController.addMessage("Add airport succesfully!");

                    airport = new Airport();
                    city = new City();
                    country = new Country();
                    airports = new ArrayList();
                    cities = new ArrayList();
                } else {
                    msgController.addErrorMessage("Failed to add city!");
                }
            } else {
                msgController.addErrorMessage("ICAO code exists!");
            }
        } else {
            msgController.addErrorMessage("IATA code exists!");
        }

    }

    public String capitalize(String source) {
        StringBuilder res = new StringBuilder();
        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }
        System.out.println(res.toString());
        return res.toString();
    }

    public String onFlowProcess(FlowEvent event) {
        System.out.println("onFlowProcess: " + skip);
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
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
        if (country != null) {
            cities = routePlanningSession.getCityListByCountry(country.getIsoCode());
        }
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

    /**
     * @return the newCountry
     */
    public Country getNewCountry() {
        return newCountry;
    }

    /**
     * @param newCountry the newCountry to set
     */
    public void setNewCountry(Country newCountry) {
        this.newCountry = newCountry;
    }

    /**
     * @return the newCity
     */
    public City getNewCity() {
        return newCity;
    }

    /**
     * @param newCity the newCity to set
     */
    public void setNewCity(City newCity) {
        this.newCity = newCity;
    }

}
