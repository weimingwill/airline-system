/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.BiddingSession;
import ams.afos.entity.FlightCrew;
import ams.afos.entity.Pairing;
import ams.afos.entity.PairingFlightCrew;
import ams.afos.session.FlightCrewSessionLocal;
import ams.aps.util.exception.EmptyTableException;
import ams.aps.util.helper.AircraftStatus;
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
import javax.servlet.http.HttpServletRequest;
import managedbean.application.MsgController;
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
    private List<PairingFlightCrew> histBids;

    /**
     * Creates a new instance of FlightCrewBacking
     */
    public FlightCrewBacking() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf('.', uri.lastIndexOf("/")));
        System.out.println("FleetTableController: init() uri = " + uri);
        switch (uri) {
            case "viewFlightCrewProfile":
                getAllFlightCrew();
                break;
            case "viewBidHist":
                getAllBiddingHist();
        }

    }

    private void getAllBiddingHist() {
        setHistBids(flightCrewSession.getFlightCrewBiddingHistory(getCurrFlightCrew()));
    }

    public FlightCrew getCurrFlightCrew() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        String username = (String) sessionMap.get("username");
        System.out.println("Flight Crew username: " + username);
        return flightCrewSession.getFlightCrewByUsername(username);
    }

    public void onCrewTableRowSelect(SelectEvent event) {
        if (selectedCrew != null) {
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

    public Pairing getPairingById(Long id) {
        return flightCrewSession.getPairingById(id);
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

    /**
     * @return the histBids
     */
    public List<PairingFlightCrew> getHistBids() {
        return histBids;
    }

    /**
     * @param histBids the histBids to set
     */
    public void setHistBids(List<PairingFlightCrew> histBids) {
        this.histBids = histBids;
    }



}
