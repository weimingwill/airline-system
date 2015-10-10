///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package managedbean.aps;
//
//import ams.aps.entity.AircraftType;
//import ams.aps.entity.Route;
//import ams.aps.session.FlightSchedulingSessionLocal;
//import javax.inject.Named;
//import javax.enterprise.context.SessionScoped;
//import java.io.Serializable;
//import java.util.List;
//import javax.ejb.EJB;
//import javax.faces.event.ActionEvent;
//import javax.inject.Inject;
//import managedbean.application.MsgController;
//
///**
// *
// * @author ChuningLiu
// */
//@Named(value = "aircraftController")
//@SessionScoped
//public class AircraftController implements Serializable {
//
//    @Inject
//    private MsgController msgController;
//
//    @EJB
//    private FlightSchedulingSessionLocal routeSchedulingSession;
//    
//    
//    private List<AircraftType> typeList;
//    private AircraftType type;
//    private Route route;
//    private double frequency;
//    private double maxFrequency;
//    
//    /**
//     * Creates a new instance of AircraftController
//     */
//    public AircraftController() {
//    }
//    
//    public void getCapableAircraftType() {
//        setTypeList(routeSchedulingSession.getCapableAircraftTypesForRoute(getRoute()));
//    }
//    
//    public void limitMaxFlightFrequency() {
//        maxFrequency = routeSchedulingSession.getMaxFlightFrequency(type, route);
//    }
//    
//    public void onFrequencyChange(){
//        if (frequency > maxFrequency) {
//            msgController.addErrorMessage("Exceed maximum frequency limitation!");
//        }
//    }
//
//    /**
//     * @return the typeList
//     */
//    public List<AircraftType> getTypeList() {
//        return typeList;
//    }
//
//    /**
//     * @param typeList the typeList to set
//     */
//    public void setTypeList(List<AircraftType> typeList) {
//        this.typeList = typeList;
//    }
//
//    /**
//     * @return the type
//     */
//    public AircraftType getType() {
//        return type;
//    }
//
//    /**
//     * @param type the type to set
//     */
//    public void setType(AircraftType type) {
//        this.type = type;
//    }
//
//    /**
//     * @return the route
//     */
//    public Route getRoute() {
//        return route;
//    }
//
//    /**
//     * @param route the route to set
//     */
//    public void setRoute(Route route) {
//        this.route = route;
//    }
//
//    /**
//     * @param maxFrequency the maxFrequency to set
//     */
//    public void setMaxFrequency(double maxFrequency) {
//        this.maxFrequency = maxFrequency;
//    }
//
//    /**
//     * @return the maxFrequency
//     */
//    public double getMaxFrequency() {
//        return maxFrequency;
//    }
//    
//    
//}
