/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.aps.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ChuningLiu
 */
@Entity
public class Airport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icaoCode;
    private String iataCode;
    private String airportName;
    private Boolean isHub;
    private Float latitude;
    private Float altitude;
    private Float longitude;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private City city;
    
    @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Country country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Airport)) {
            return false;
        }
        Airport other = (Airport) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.aps.entity.Airport[ id=" + id + " ]";
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getAltitude() {
        return altitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the icaoCode
     */
    public String getIcaoCode() {
        return icaoCode;
    }

    /**
     * @param icaoCode the icaoCode to set
     */
    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    /**
     * @return the iataCode
     */
    public String getIataCode() {
        return iataCode;
    }

    /**
     * @param iataCode the iataCode to set
     */
    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    /**
     * @return the airportName
     */
    public String getAirportName() {
        return airportName;
    }

    /**
     * @param airportName the airportName to set
     */
    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    /**
     * @return the isHub
     */
    public Boolean getIsHub() {
        return isHub;
    }

    /**
     * @param isHub the isHub to set
     */
    public void setIsHub(Boolean isHub) {
        this.isHub = isHub;
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
    
}
