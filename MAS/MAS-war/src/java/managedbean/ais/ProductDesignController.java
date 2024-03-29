/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.TicketFamily;
import ams.ais.util.exception.NeedTicketFamilyException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchCabinClassTicketFamilyException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.CabinClassTicketFamilyHelper;
import ams.aps.entity.Aircraft;
import ams.ais.session.ProductDesignSessionLocal;
import ams.aps.util.exception.NoSuchAircraftCabinClassException;
import ams.aps.util.exception.NoSuchAircraftException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author winga_000
 */
@Named(value = "productDesignController")
@SessionScoped
public class ProductDesignController implements Serializable {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    @EJB
    private ProductDesignSessionLocal productDesignSession;

    private List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers;
    private Aircraft selectedAircraft = new Aircraft();

    /**
     * Creates a new instance of ProductDesignController
     */
    public ProductDesignController() {
    }

    public void onAircraftChange() {
        try {
            cabinClassTicketFamilyHelpers = productDesignSession.getCabinClassTicketFamilyHelpers(selectedAircraft.getAircraftId());
        } catch (NoSuchCabinClassException | NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public String assignAircraftTicketFamily() {
        try {
            productDesignSession.assignAircraftTicketFamily(selectedAircraft, cabinClassTicketFamilyHelpers);
            cleanAttribute();
            msgController.addMessage("Assign ticket family to aircraft successfully!");
        } catch (NeedTicketFamilyException | NoSuchAircraftCabinClassException | NoSuchCabinClassTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
            return "";
        }
        return navigationController.redirectToProductDesign();
    }

    public List<Aircraft> getScheduledAircrafts() {
        List<Aircraft> aircrafts;
        try {
            aircrafts = productDesignSession.getScheduledAircrafts();
        } catch (NoSuchAircraftException ex) {
            aircrafts = new ArrayList<>();
        }
        return aircrafts;
    }

    public List<TicketFamily> getAircraftTicketFamilys() {
        List<TicketFamily> ticketFamilys = new ArrayList<>();
        try {
            if (selectedAircraft != null) {
                ticketFamilys = productDesignSession.getAircraftTicketFamilys(selectedAircraft.getAircraftId());
            }
        } catch (NoSuchTicketFamilyException ex) {
        }
        System.out.print("get ticket family" +ticketFamilys);
        return ticketFamilys;
    }

    public void cleanAttribute() {
        selectedAircraft = null;
        cabinClassTicketFamilyHelpers = null;
    }

    //Getter and setter
    public List<CabinClassTicketFamilyHelper> getCabinClassTicketFamilyHelpers() {
        return cabinClassTicketFamilyHelpers;
    }

    public void setCabinClassTicketFamilyHelpers(List<CabinClassTicketFamilyHelper> cabinClassTicketFamilyHelpers) {
        this.cabinClassTicketFamilyHelpers = cabinClassTicketFamilyHelpers;
    }

    public Aircraft getSelectedAircraft() {
        return selectedAircraft;
    }

    public void setSelectedAircraft(Aircraft selectedAircraft) {
        this.selectedAircraft = selectedAircraft;
    }
}
