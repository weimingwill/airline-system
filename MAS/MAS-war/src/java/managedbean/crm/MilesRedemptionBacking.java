/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.CabinClass;
import ams.ais.session.ProductDesignSessionLocal;
import ams.aps.entity.Airport;
import ams.aps.session.RoutePlanningSessionLocal;
import ams.crm.util.exception.NoSuchRegCustException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.common.EmailController;

/**
 *
 * @author Bowen
 */
@Named(value = "milesRedemptionBacking")
@ViewScoped
public class MilesRedemptionBacking implements Serializable {

    @Inject
    MsgController msgController;
    @Inject
    private CustomerController customerController;
    @Inject
    private EmailController emailController;
    

    @EJB
    private RoutePlanningSessionLocal routePlanningSession;
    @EJB
    private ProductDesignSessionLocal productDesignSession;
    
    
    
    private Airport deptAirport = new Airport();
    private Airport arrAirport = new Airport();
    private List<Airport> allAirports;
    private List<CabinClass> cabinClses;
    private CabinClass selectedCabinCls;
    private Double calculatedMiles;
    private Double actualPointNeed;

    

    /**
     * Creates a new instance of MilesRedemptionBacking
     */
    public MilesRedemptionBacking() {
    }
    @PostConstruct
    public void init() {
        allAirports = routePlanningSession.getAllAirports();
        cabinClses = productDesignSession.getAllCabinClass();
        deptAirport = routePlanningSession.getAirportByICAOCode("WSSS");
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
    public void mileRedemption() {

        if (customerController.getAccMiles() < actualPointNeed) {

            msgController.addErrorMessage("Sorry, you don't have enough points");

        } else {
            try {
                customerController.updateMiles();
            } catch (NoSuchRegCustException ex) {
                Logger.getLogger(RedeemMilesBacking.class.getName()).log(Level.SEVERE, null, ex);
            }
            msgController.addMessage("Congratulations! You have successfully redeem your points!");
            String subject = "[Important] Merlion Airline Receive your redemption Code";
            String mailContent = "Dear Customer: \n Please use the following code for your ticket redemption: \n"  + "please kindly use this code\n" + "&email=\n" ;
            String receiver = customerController.getEmail();
            emailController.sendEmail(subject, mailContent, receiver);
        }
    }
    
    public void mileCalculation(){
        calculatedMiles = routePlanningSession.distance(arrAirport, deptAirport);
        calculatedMiles = calculatedMiles * 0.000621371;
        actualPointNeed = calculatedMiles *4 ;
        if(selectedCabinCls.getName().equals("Merlion Sky Palace")){
            actualPointNeed=actualPointNeed*8;
        }
        else if(selectedCabinCls.getName().equals("Business")) {
            actualPointNeed=actualPointNeed * 5;
        }
        else if(selectedCabinCls.getName().equals("Premium Economy")) {
            actualPointNeed = actualPointNeed * 3;
        }
        System.out.println("calculated mile is"+calculatedMiles);
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

    public List<Airport> getAllAirports() {
        return allAirports;
    }

    public void setAllAirports(List<Airport> allAirports) {
        this.allAirports = allAirports;
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
    public Double getCalculatedMiles() {
        return calculatedMiles;
    }

    public void setCalculatedMiles(Double calculatedMiles) {
        this.calculatedMiles = calculatedMiles;
    }
    
    public Double getActualPointNeed() {
        return actualPointNeed;
    }

    public void setActualPointNeed(Double actualPointNeed) {
        this.actualPointNeed = actualPointNeed;
    }
}
