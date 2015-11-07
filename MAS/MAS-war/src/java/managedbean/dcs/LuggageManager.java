/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.dcs;

import ams.ars.entity.AirTicket;
import ams.crm.entity.Customer;
import ams.dcs.entity.CheckInLuggage;
import ams.dcs.session.CheckInSessionLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.DcsNavController;
import managedbean.application.MsgController;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "luggageManager")
@SessionScoped
public class LuggageManager implements Serializable {

    @Inject
    private MsgController msgController;

    @Inject
    private DcsNavController dcsNavController;

    @EJB
    private CheckInSessionLocal checkInSession;

    private double weight;
    private double totalWeight = 0;
    private String remark;
    
    private Customer passenger;

    private AirTicket airTicket;
    private List<AirTicket> airTickets = new ArrayList();
    private CheckInLuggage luggage;
    private List<CheckInLuggage> luggageList = new ArrayList();

    /**
     * Creates a new instance of LuggageManager
     */
    public LuggageManager() {
    }

    @PostConstruct
    public void init() {
        ticketReceived();
    }

    public void ticketReceived() {
        boolean refresh = false;
        if (!airTickets.isEmpty()) {
            refresh = true;
        }
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<AirTicket> temp = (ArrayList<AirTicket>) map.get("checkedTickets");
        if (!temp.isEmpty()) {
            setAirTickets(temp);
            if (refresh) {
                airTicket = null;
                luggage = null;
                luggageList.clear();
            }
        }
    }

    public void addLuggage() {
        luggage.setRealWeight(weight);
        if (remark != null) {
            luggage.setRemark(remark);
        }
        luggageList.add(luggage);
        totalWeight += weight;
        weight = 0;
        remark = "";
    }

    /**
     * @return the luggage
     */
    public CheckInLuggage getLuggage() {
        return luggage;
    }

    /**
     * @param luggage the luggage to set
     */
    public void setLuggage(CheckInLuggage luggage) {
        this.luggage = luggage;
    }

    /**
     * @return the luggageList
     */
    public List<CheckInLuggage> getLuggageList() {
        return luggageList;
    }

    /**
     * @param luggageList the luggageList to set
     */
    public void setLuggageList(List<CheckInLuggage> luggageList) {
        this.luggageList = luggageList;
    }

    /**
     * @return the airTicket
     */
    public AirTicket getAirTicket() {
        return airTicket;
    }

    /**
     * @param airTicket the airTicket to set
     */
    public void setAirTicket(AirTicket airTicket) {
        this.airTicket = airTicket;
    }

    /**
     * @return the airTickets
     */
    public List<AirTicket> getAirTickets() {
        return airTickets;
    }

    /**
     * @param airTickets the airTickets to set
     */
    public void setAirTickets(List<AirTicket> airTickets) {
        this.airTickets = airTickets;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the totalWeight
     */
    public double getTotalWeight() {
        return totalWeight;
    }

    /**
     * @param totalWeight the totalWeight to set
     */
    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * @return the passenger
     */
    public Customer getPassenger() {
        return passenger;
    }

    /**
     * @param passenger the passenger to set
     */
    public void setPassenger(Customer passenger) {
        this.passenger = passenger;
    }

}
