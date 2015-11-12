/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.FlightCrew;
import ams.afos.session.FlightCrewSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.common.UserController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewBacking")
@ViewScoped
public class FlightCrewBacking implements Serializable {

    @EJB
    private FlightCrewSessionLocal flightCrewSession;

    @Inject
    private MsgController msgController;


    private List<FlightCrew> flightCrews;
    private FlightCrew selectedCrew;

    /**
     * Creates a new instance of FlightCrewBacking
     */
    public FlightCrewBacking() {
    }

    @PostConstruct
    public void init() {
        getAllFlightCrew();
    }

    public FlightCrew getCurrFlightCrew(){
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        String username = (String) sessionMap.get("username");
        System.out.println("Flight Crew username: " + username);
        return flightCrewSession.getFlightCrewByUsername(username);
    }
    
    public void onCrewTableRowSelect(SelectEvent event) {
        if(selectedCrew != null){
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('crewProfileDlg').show();");
            context.update("crewProfileDlg");
        }
    }

    public void getAllFlightCrew() {
        try {
            setFlightCrews(flightCrewSession.getAllFlightCrew());
        } catch (EmptyTableException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    /**
     * @return the flightCrews
     */
    public List<FlightCrew> getFlightCrews() {
        return flightCrews;
    }

    /**
     * @param flightCrews the flightCrews to set
     */
    public void setFlightCrews(List<FlightCrew> flightCrews) {
        this.flightCrews = flightCrews;
    }

    /**
     * @return the selectedCrew
     */
    public FlightCrew getSelectedCrew() {
        return selectedCrew;
    }

    /**
     * @param selectedCrew the selectedCrew to set
     */
    public void setSelectedCrew(FlightCrew selectedCrew) {
        this.selectedCrew = selectedCrew;
    }

}
