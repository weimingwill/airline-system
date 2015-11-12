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
import ams.dcs.util.exception.NoSuchPNRException;
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
    private double excessWeight = 0;
    private double excessWeightPrice = 0;
    private double totalExcessWeightPrice = 0;

    private String remark;
    private long ticketID;

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
        if (airTicket != null) {
            refresh = true;
        }
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        AirTicket temp = (AirTicket) map.get("checkedTicket");
        if (temp != null) {
            setAirTicket(temp);
            airTickets.clear();
            airTickets.add(airTicket);
            if (refresh) {
                setPassenger((Customer) map.get("passenger"));
                setLuggage(new CheckInLuggage());
                getLuggageList().clear();
            }
        }
    }

    public String searchTicket() {
        try {
            AirTicket a = checkInSession.searchTicketByID(ticketID);
            if (a == null) {
                msgController.addErrorMessage("No PNR founded!");
                return "";
            }else if (!a.getStatus().equals("Checked in")) {
                msgController.addErrorMessage("Air ticket not checked in!");
                return "";
            }else{
                setAirTicket(a);
                return dcsNavController.toCheckInLuggage();
            }
        } catch (NoSuchPNRException e) {
            msgController.addErrorMessage("No PNR found!");
            return "";
        }
    }

    public void addLuggage() {
        if (weight > 53) {
            msgController.addErrorMessage("Luggage weight excesses 53 kg limitation!");
        } else {
            getLuggage().setRealWeight(getWeight());
            if (getRemark() != null) {
                getLuggage().setRemark(getRemark());
                remark = "";
            }
            getLuggageList().add(getLuggage());
            setLuggage(new CheckInLuggage());
            setExcessWeight(0);
            setExcessWeightPrice(0);

            for (AirTicket a : getAirTickets()) {
                excessWeightPrice = checkInSession.calculateLuggagePrice(a, luggageList);
                totalExcessWeightPrice += excessWeightPrice;
            }
        }
    }

    public void checkInLuggage() {
        boolean flag = true;
        for (AirTicket a : airTickets) {
            Double price = checkInSession.calculateLuggagePrice(a, luggageList);
            if (!checkInSession.checkInLuggage(a, luggageList, price)) {
                msgController.addErrorMessage("Check In error for ticket " + a.getId() + " !");
                flag = false;
            }
        }
        if (flag) {
            msgController.addMessage("Check in luggage successfully!");
            cleanVariables();
        }
    }

    private void cleanVariables() {
        setAirTicket(new AirTicket());
        setAirTickets(new ArrayList<>());
        setExcessWeight(0);
        setExcessWeightPrice(0);
        setLuggage(new CheckInLuggage());
        setLuggageList(new ArrayList<>());
        setPassenger(new Customer());
        setRemark("");
        setTotalExcessWeightPrice(0);
        setWeight(0);
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
     * @return the excessWeight
     */
    public double getExcessWeight() {
        return excessWeight;
    }

    /**
     * @param excessWeight the excessWeight to set
     */
    public void setExcessWeight(double excessWeight) {
        this.excessWeight = excessWeight;
    }

    /**
     * @return the excessWeightPrice
     */
    public double getExcessWeightPrice() {
        return excessWeightPrice;
    }

    /**
     * @param excessWeightPrice the excessWeightPrice to set
     */
    public void setExcessWeightPrice(double excessWeightPrice) {
        this.excessWeightPrice = excessWeightPrice;
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
     * @return the totalExcessWeightPrice
     */
    public double getTotalExcessWeightPrice() {
        return totalExcessWeightPrice;
    }

    /**
     * @param totalExcessWeightPrice the totalExcessWeightPrice to set
     */
    public void setTotalExcessWeightPrice(double totalExcessWeightPrice) {
        this.totalExcessWeightPrice = totalExcessWeightPrice;
    }

    /**
     * @return the ticketID
     */
    public long getTicketID() {
        return ticketID;
    }

    /**
     * @param ticketID the ticketID to set
     */
    public void setTicketID(long ticketID) {
        this.ticketID = ticketID;
    }

}
