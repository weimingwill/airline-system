/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.helper.MilesRedemption;
import ams.crm.util.exception.NoEnoughPointException;
import ams.crm.util.exception.NoSuchRegCustException;
import ams.crm.util.helper.CrmMsg;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.common.EmailController;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Bowen
 */
@Named(value = "redeemMilesController")
@ViewScoped
public class RedeemMilesBacking implements Serializable {

    @Inject
    private CustomerController customerController;
    @Inject
    private MsgController msgController;
    @Inject
    private EmailController emailController;
    
    private MilesRedemption selectedMilesRedemption;

    public void mileRedemption() {

        if (customerController.getAccMiles() < selectedMilesRedemption.getMiles()) {

            msgController.addErrorMessage("Sorry, you don't have enough points");

        } else {
            try {
                customerController.updateMiles();
            } catch (NoSuchRegCustException ex) {
                Logger.getLogger(RedeemMilesBacking.class.getName()).log(Level.SEVERE, null, ex);
            }
            msgController.addMessage("Congratulations! You have successfully redeem your points!");
            String subject = "[Important] Merlion Airline Receive your redemption Code";
            String mailContent = "Dear Customer: \n Please use the following code for your ticket redemption: \n"  + "please kindly use this code\n" + "&email=\n" + selectedMilesRedemption.getPromoCode();
            String receiver = customerController.getEmail();
            emailController.sendEmail(subject, mailContent, receiver);
        }
    }

    public void onRedeemBtnClick(String dialogId) {
        if (selectedMilesRedemption != null) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF(\'" + dialogId + "\').show()");
            context.update(dialogId);
        }
    }

    public List<MilesRedemption> getMilesRedemptions() {
        List<MilesRedemption> milesRedemptions = new ArrayList();
        milesRedemptions.add(new MilesRedemption("Singapore", "Perth", 2431.0, "SIN-PER-01"));
        milesRedemptions.add(new MilesRedemption("Perth", "Singapore", 2431.0, "PER-SIN-02"));
        milesRedemptions.add(new MilesRedemption("Singapore", "Beijing", 2777.0, "SIN-PEK-01"));
        milesRedemptions.add(new MilesRedemption("Beijing", "Singapore", 7364.0, "PEK-SIN-02"));
        milesRedemptions.add(new MilesRedemption("New York", "Shanghai", 7364.0, "JFK-PVG-01"));
        milesRedemptions.add(new MilesRedemption("Shanghai", "New York", 7364.0, "PVG-JFK-02"));
        return milesRedemptions;
    }

    public MilesRedemption getSelectedMilesRedemption() {
        return selectedMilesRedemption;
    }

    public void setSelectedMilesRedemption(MilesRedemption selectedMilesRedemption) {
        this.selectedMilesRedemption = selectedMilesRedemption;
    }

    public List<String> getOriginalList() {
        ArrayList<String> originalList = new ArrayList();
        originalList.add("Singapore");
        originalList.add("Perth");
        originalList.add("Singapore");
        originalList.add("Beijing");
        originalList.add("New York");
        originalList.add("Shanghai");
        return originalList;
    }

    public List<String> getDestinationList() {
        ArrayList<String> destinationList = new ArrayList();
        destinationList.add("Perth");
        destinationList.add("Singapore");
        destinationList.add("Beijing");
        destinationList.add("Singapore");
        destinationList.add("Shanghai");
        destinationList.add("New York");
        return destinationList;
    }

    public List<Double> getMileList() {
        ArrayList<Double> mileList = new ArrayList();
        mileList.add(2431.0);
        mileList.add(2431.0);
        mileList.add(2777.0);
        mileList.add(2777.0);
        mileList.add(7364.0);
        mileList.add(7364.0);
        return mileList;
    }

    public List<String> getPromoCodeList() {
        ArrayList<String> promoCodeList = new ArrayList();
        promoCodeList.add("SIN-PER-01");
        promoCodeList.add("SIN-PER-02");
        promoCodeList.add("SIN-PEK-01");
        promoCodeList.add("PER-SIN-02");
        promoCodeList.add("JFK-PVG-01");
        promoCodeList.add("PVG-JFK-02");
        return promoCodeList;
    }

    /**
     * Creates a new instance of RedeemMilesController
     */
    public RedeemMilesBacking() {
    }

}
