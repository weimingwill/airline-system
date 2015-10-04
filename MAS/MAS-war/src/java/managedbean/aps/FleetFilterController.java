/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.util.helper.RetireAircraftFilterHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Lewis
 */
@Named(value = "fleetFilterController")
@RequestScoped
public class FleetFilterController implements Serializable {

    @EJB
    private FleetPlanningSessionLocal fleetPlanningSession;

    RetireAircraftFilterHelper retireAircraftFilterHelper = new RetireAircraftFilterHelper();

    /**
     * Creates a new instance of FleetFilterController
     */
    public FleetFilterController() {

    }

    @PostConstruct
    public void init() {
        setInitialValue();
    }
    
    public List<Aircraft> getFilteredAircrafts(){
        printRetireAircraftFilters();
        List<Aircraft> filteredAircraft = fleetPlanningSession.filterAircraftsForRetire(retireAircraftFilterHelper);
        return filteredAircraft;
    }

    public void onResetBtnClick() {
        setInitialValue();
    }

    public void setInitialValue() {
        retireAircraftFilterHelper.setTimesOfMaint(0);
        retireAircraftFilterHelper.setNumOfFlightCycle(0);
        retireAircraftFilterHelper.setMaxTotalFlightDist(1000);
        retireAircraftFilterHelper.setTypeOfMaint(new ArrayList<String>());
        fleetPlanningSession.initRetireAicraftFilter(retireAircraftFilterHelper);
    }

    private void printRetireAircraftFilters() {
        System.out.println("FleetFilterController: printRetireAircraftFilters()");
        System.out.println("fromAddOnDate = " + retireAircraftFilterHelper.getFromAddOnDate());
        System.out.println("minLifespan = " + retireAircraftFilterHelper.getMinLifespan());
        System.out.println("maxLifespan = " + retireAircraftFilterHelper.getMaxLifespan());
        System.out.println("minAvgFuelUsage = " + retireAircraftFilterHelper.getMinAvgFuelUsage());
        System.out.println("minFuelCapacity = " + retireAircraftFilterHelper.getMinFuelCapacity());
        System.out.println("selectedReasonsforMaint = " + retireAircraftFilterHelper.getTypeOfMaint());
        System.out.println("timesOfMaint = " + retireAircraftFilterHelper.getTimesOfMaint());
        System.out.println("selectedMaxTotalFlightDist = " + retireAircraftFilterHelper.getMaxTotalFlightDist());
        System.out.println("numOfFlightCycle = " + retireAircraftFilterHelper.getNumOfFlightCycle());
    }

    public RetireAircraftFilterHelper getRetireAircraftFilterHelper() {
        return retireAircraftFilterHelper;
    }

    public void setRetireAircraftFilterHelper(RetireAircraftFilterHelper retireAircraftFilterHelper) {
        this.retireAircraftFilterHelper = retireAircraftFilterHelper;
    }


}
