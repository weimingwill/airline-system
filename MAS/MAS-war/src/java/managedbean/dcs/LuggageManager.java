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
import ams.dcs.util.exception.NoSuchBoardingPassException;
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
    private double totalWeight;
    private double excessWeight = 0;
    private double excessWeightPrice = 0;
    private double totalExcessWeightPrice = 0;

    private String remark;
    private long passID;
    private String idMethod;
    private long reference;

    private Customer passenger;

    private AirTicket airTicket;
    private List<AirTicket> airTickets = new ArrayList();
    private List<AirTicket> selectedAirTickets = new ArrayList();
    private CheckInLuggage luggage;
    private List<CheckInLuggage> luggageToBeRemoved = new ArrayList();
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

    public String searchPass() {
        try {
            AirTicket a = checkInSession.getAirTicketByPassID(passID);
            if (a == null) {
                msgController.addErrorMessage("Boarding pass not found!");
                return "";
            } else if (!a.getStatus().equals("Checked-in")) {
                msgController.addErrorMessage("The Air ticket is boarded already!");
                return "";
            } else {
                cleanVariables();
                setAirTicket(a);
                setPassenger(airTicket.getCustomer());
                airTickets.add(airTicket);
                return dcsNavController.toCheckInLuggage();
            }
        } catch (NoSuchBoardingPassException e) {
            msgController.addErrorMessage("Boarding pass not found!");
            return "";
        }
    }

    public void addLuggage() {
        if (weight > 53) {
            msgController.addErrorMessage("Luggage weight excesses limitation!");
        } else {
            getLuggage().setRealWeight(getWeight());
            totalWeight += getWeight();
            if (getRemark() != null) {
                getLuggage().setRemark(getRemark());
                remark = "";
            }
            getLuggageList().add(getLuggage());
            setLuggage(new CheckInLuggage());
            setWeight(0);
            setExcessWeight(0);
            setExcessWeightPrice(0);

            for (AirTicket a : getAirTickets()) {
                excessWeightPrice = checkInSession.calculateLuggagePrice(a, luggageList);
                System.out.println("price: "+excessWeightPrice);
                totalExcessWeightPrice += excessWeightPrice;
            }
        }
    }

    public void checkInLuggage() {
        boolean flag = true;
        for (AirTicket a : airTickets) {
            Double price = checkInSession.calculateLuggagePrice(a, luggageList);
            System.out.println("price: "+price);
            if (!checkInSession.checkInLuggage(a, luggageList, price)) {
                msgController.addErrorMessage("Check In error for boarding pass No. " + a.getBoardingPass().getId() + "!");
                flag = false;
            }
        }
        if (flag) {
            msgController.addMessage("Check in luggage successfully!");
            cleanVariables();
        }
    }

    public String getLuggages() {
        if (idMethod.equals("pass")) {
            try {
                airTicket = checkInSession.getAirTicketByPassID(reference);
                passenger = airTicket.getCustomer();
            } catch (Exception e) {
                msgController.addErrorMessage("No Such Boarding Pass Found!");
                return "";
            }
        } else {
            try {
                airTicket = checkInSession.getAirTicketByID(reference);
                passenger = airTicket.getCustomer();
            } catch (Exception e) {
                msgController.addErrorMessage("No Such Air Ticket Found!");
                return "";
            }
        }
        luggageList = airTicket.getLuggages();
        return dcsNavController.toViewLuggage();
    }

    public String removeLuggage() {
        try {
            checkInSession.removeLuggage(airTicket, luggageToBeRemoved);
            msgController.addMessage("Luggage removed!");
            return dcsNavController.toSearchTicket();
        } catch (Exception e) {
            msgController.addErrorMessage("Luggage failed to be removed!");
            return "";
        }
    }

    private void cleanVariables() {
        setAirTicket(new AirTicket());
        setAirTickets(new ArrayList<>());
        setExcessWeight(0);
        setTotalExcessWeightPrice(0);
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
     * @return the passID
     */
    public long getPassID() {
        return passID;
    }

    /**
     * @param passID the passID to set
     */
    public void setPassID(long passID) {
        this.passID = passID;
    }

    /**
     * @return the selectedAirTickets
     */
    public List<AirTicket> getSelectedAirTickets() {
        return selectedAirTickets;
    }

    /**
     * @param selectedAirTickets the selectedAirTickets to set
     */
    public void setSelectedAirTickets(List<AirTicket> selectedAirTickets) {
        this.selectedAirTickets = selectedAirTickets;
    }

    /**
     * @return the idMethod
     */
    public String getIdMethod() {
        return idMethod;
    }

    /**
     * @param idMethod the idMethod to set
     */
    public void setIdMethod(String idMethod) {
        this.idMethod = idMethod;
    }

    /**
     * @return the reference
     */
    public long getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(long reference) {
        this.reference = reference;
    }

    /**
     * @return the luggageToBeRemoved
     */
    public List<CheckInLuggage> getLuggageToBeRemoved() {
        return luggageToBeRemoved;
    }

    /**
     * @param luggageToBeRemoved the luggageToBeRemoved to set
     */
    public void setLuggageToBeRemoved(List<CheckInLuggage> luggageToBeRemoved) {
        this.luggageToBeRemoved = luggageToBeRemoved;
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

}
