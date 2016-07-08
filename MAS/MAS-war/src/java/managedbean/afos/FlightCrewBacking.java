/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.afos;

import ams.afos.entity.FlightCrew;
import ams.afos.entity.Pairing;
import ams.afos.entity.PairingFlightCrew;
import ams.afos.entity.SwappingRequest;
import ams.afos.session.FlightCrewMgmtSessionLocal;
import ams.afos.session.FlightCrewSessionLocal;
import ams.afos.util.helper.SwappingReqStataus;
import ams.aps.util.exception.EmptyTableException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
import managedbean.application.NavigationController;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.event.timeline.TimelineSelectEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author Lewis
 */
@Named(value = "flightCrewBacking")
@ViewScoped
public class FlightCrewBacking implements Serializable {

    @EJB
    private FlightCrewMgmtSessionLocal flightCrewMgmtSession;

    @EJB
    private FlightCrewSessionLocal flightCrewSession;

    @Inject
    private MsgController msgController;

    @Inject
    private NavigationController navigationController;

    private List<FlightCrew> flightCrews;
    private FlightCrew selectedCrew;
    private List<PairingFlightCrew> histBids;
    private Date timelineStartDate;
    private Date timelineEndDate;
    private TimelineModel model;
    private FlightCrew currCrew;
    private List<Pairing> crewNextMonthPairings;
    private Pairing myPairing;
    private Pairing targetPairing;
    private List<Pairing> pairingsForRef;
    private SwappingRequest matchedReq;
    private List<SwappingRequest> altReqs;
    private SwappingRequest selectedSwappingRequest;
    private List<SwappingRequest> crewSwappingRequests;

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
                break;
            case "swapWorkingSlots":
                setCurrCrew(getCurrFlightCrew());
                if (currCrew != null) {
                    initTimeline();
                }
                break;
            case "manageSwapping":
                setCurrCrew(getCurrFlightCrew());
                if (currCrew != null) {
                    setCrewSwappingRequests(flightCrewSession.getAllCrewSwappingRequests(currCrew));
                }
                break;
            case "viewAllBidHist":
                setHistBids(flightCrewMgmtSession.getAllBiddingHist());
                break;
            case "viewAllSwapHist":
                setCrewSwappingRequests(flightCrewMgmtSession.getAllSwappingRequests());
                break;
        }
    }

    private void initTimeline() {
        setTimelineStartDate(getNextMonthFirstDay());
        setTimelineEndDate(getNextMonthLastDay());
        model = new TimelineModel();
        setOverallScheduleToTimeline();
        setCurrCrewScheduleToTimeline();
    }

    private void setOverallScheduleToTimeline() {
        TimelineGroup timelineGroup = new TimelineGroup("overall", "All Pairings");
        model.addGroup(timelineGroup);
        List<Pairing> allNextMonthPairings = flightCrewSession.getAllNextMonthPairings(currCrew);
        System.out.println("pairings: " + allNextMonthPairings);
        for (Pairing pairing : allNextMonthPairings) {
            model.add(new TimelineEvent(pairing, getPairingStartDate(pairing), getPairingEndDate(pairing), Boolean.FALSE, "overall"));
        }
    }

    private void setCurrCrewScheduleToTimeline() {
        TimelineGroup timelineGroup = new TimelineGroup(currCrew.getFlightCrewID(), "My Pairings");
        model.addGroup(timelineGroup);
        crewNextMonthPairings = flightCrewSession.getCrewNextMonthPairings(currCrew);
        for (Pairing pairing : crewNextMonthPairings) {
            model.add(new TimelineEvent(pairing, getPairingStartDate(pairing), getPairingEndDate(pairing), Boolean.FALSE, currCrew.getFlightCrewID()));
        }
    }

    public void onPairingClick(TimelineSelectEvent e) {
        TimelineEvent event = e.getTimelineEvent();
        Pairing selectedPairing = (Pairing) event.getData();
        if (crewNextMonthPairings.contains(selectedPairing)) {
            System.out.println("Crew pairing selected");
            setMyPairing(selectedPairing);
        } else {
            System.out.println("Non crew pairing selected");
            setTargetPairing(selectedPairing);
        }
    }

    public Date getPairingStartDate(Pairing pairing) {
        if (pairing != null) {
            return pairing.getFlightDuties().get(0).getReportTime();
        } else {
            return null;
        }
    }

    public Date getPairingEndDate(Pairing pairing) {
        if (pairing != null) {
            return pairing.getFlightDuties().get(pairing.getFlightDuties().size() - 1).getDismissTime();
        } else {
            return null;
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

    private Date getNextMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    private Date getNextMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public void onSwapBtnClick() {
        if (myPairing == null || targetPairing == null) {
            msgController.addErrorMessage("Please select pairings to swap");
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            getMatchedRequest();
            if (matchedReq == null) {
                getAlternativeRequests();
                context.update("altReqDialog");
                context.execute("PF('altReqDialog').show()");
            } else {
                //TODO: display dialog with matched request to confirm swapping
                context.update("matchedReqDialog");
                context.execute("PF('matchedReqDialog').show()");
            }
        }
    }

    private void getMatchedRequest() {
        List<SwappingRequest> temp = flightCrewSession.getMatchedSwappingReqs(myPairing, targetPairing, crewNextMonthPairings);
        if (temp != null && !temp.isEmpty()) {
            setMatchedReq(temp.get(0));
        } else {
            setMatchedReq(null);
        }
    }

    private void getAlternativeRequests() {
        setAltReqs(flightCrewSession.getAlternativeSwappingReqs(myPairing, crewNextMonthPairings));
    }

    public void sendSwappingRequest() {
        SwappingRequest request = new SwappingRequest();
        request.setChosenPairing(myPairing);
        request.setTargetPairing(targetPairing);
        request.setCreatedTime(Calendar.getInstance().getTime());
        request.setStatus(SwappingReqStataus.PENDING);
        request.setSender(currCrew);
        flightCrewSession.sendSwappingRequest(request);
        msgController.addMessage("Send Swapping Request");
    }

    public String matchSwappingRequest(String type) {
        System.out.println("type:" + type);
        switch (type) {
            case "M":
                matchedReq.setReceiver(currCrew);
                matchedReq.setStatus(SwappingReqStataus.SWAPPED);
                flightCrewSession.matchSelectedSwappingReq(matchedReq);
                break;
            case "A":
                selectedSwappingRequest.setReceiver(currCrew);
                selectedSwappingRequest.setStatus(SwappingReqStataus.SWAPPED);
                flightCrewSession.matchSelectedSwappingReq(selectedSwappingRequest);
                break;
        }
        msgController.addMessage("Swapped!");
        return navigationController.redirectToCurrentPage();
    }

    public String onDeleteReqBtnClick() {
        flightCrewSession.cancelSwappingRequest(selectedSwappingRequest);
        msgController.addMessage("Cancel Swapping Request with ID: " + selectedSwappingRequest.getId());
        return navigationController.redirectToCurrentPage();
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

    /**
     * @return the timelineStartDate
     */
    public Date getTimelineStartDate() {
        return timelineStartDate;
    }

    /**
     * @param timelineStartDate the timelineStartDate to set
     */
    public void setTimelineStartDate(Date timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
    }

    /**
     * @return the timelineEndDate
     */
    public Date getTimelineEndDate() {
        return timelineEndDate;
    }

    /**
     * @param timelineEndDate the timelineEndDate to set
     */
    public void setTimelineEndDate(Date timelineEndDate) {
        this.timelineEndDate = timelineEndDate;
    }

    /**
     * @return the model
     */
    public TimelineModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(TimelineModel model) {
        this.model = model;
    }

    /**
     * @return the currCrew
     */
    public FlightCrew getCurrCrew() {
        return currCrew;
    }

    /**
     * @param currCrew the currCrew to set
     */
    public void setCurrCrew(FlightCrew currCrew) {
        this.currCrew = currCrew;
    }

    /**
     * @return the crewNextMonthPairings
     */
    public List<Pairing> getCrewNextMonthPairings() {
        return crewNextMonthPairings;
    }

    /**
     * @param crewNextMonthPairings the crewNextMonthPairings to set
     */
    public void setCrewNextMonthPairings(List<Pairing> crewNextMonthPairings) {
        this.crewNextMonthPairings = crewNextMonthPairings;
    }

    /**
     * @return the myPairing
     */
    public Pairing getMyPairing() {
        return myPairing;
    }

    /**
     * @param myPairing the myPairing to set
     */
    public void setMyPairing(Pairing myPairing) {
        this.myPairing = myPairing;
    }

    /**
     * @return the targetPairing
     */
    public Pairing getTargetPairing() {
        return targetPairing;
    }

    /**
     * @param targetPairing the targetPairing to set
     */
    public void setTargetPairing(Pairing targetPairing) {
        this.targetPairing = targetPairing;
    }

    /**
     * @return the pairingsForRef
     */
    public List<Pairing> getPairingsForRef() {
        return pairingsForRef;
    }

    /**
     * @param pairingsForRef the pairingsForRef to set
     */
    public void setPairingsForRef(List<Pairing> pairingsForRef) {
        this.pairingsForRef = pairingsForRef;
    }

    /**
     * @return the altReqs
     */
    public List<SwappingRequest> getAltReqs() {
        return altReqs;
    }

    /**
     * @param altReqs the altReqs to set
     */
    public void setAltReqs(List<SwappingRequest> altReqs) {
        this.altReqs = altReqs;
    }

    /**
     * @return the selectedSwappingRequest
     */
    public SwappingRequest getSelectedSwappingRequest() {
        return selectedSwappingRequest;
    }

    /**
     * @param selectedSwappingRequest the selectedSwappingRequest to set
     */
    public void setSelectedSwappingRequest(SwappingRequest selectedSwappingRequest) {
        this.selectedSwappingRequest = selectedSwappingRequest;
    }

    /**
     * @return the matchedReq
     */
    public SwappingRequest getMatchedReq() {
        return matchedReq;
    }

    /**
     * @param matchedReq the matchedReq to set
     */
    public void setMatchedReq(SwappingRequest matchedReq) {
        this.matchedReq = matchedReq;
    }

    /**
     * @return the crewSwappingRequests
     */
    public List<SwappingRequest> getCrewSwappingRequests() {
        return crewSwappingRequests;
    }

    /**
     * @param crewSwappingRequests the crewSwappingRequests to set
     */
    public void setCrewSwappingRequests(List<SwappingRequest> crewSwappingRequests) {
        this.crewSwappingRequests = crewSwappingRequests;
    }

}
