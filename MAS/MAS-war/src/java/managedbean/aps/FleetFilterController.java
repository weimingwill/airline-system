/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.Aircraft;
import ams.aps.entity.AircraftType;
import ams.aps.session.FleetPlanningSessionLocal;
import ams.aps.util.helper.AircraftModelFilterHelper;
import ams.aps.util.helper.RetireAircraftFilterHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    private RetireAircraftFilterHelper retireAircraftFilterHelper = new RetireAircraftFilterHelper();
    private AircraftModelFilterHelper aircraftModelFilterHelper = new AircraftModelFilterHelper();
    private List<String> manufacturerList;
    private List<String> typeFamilyList;

    @PostConstruct
    public void init() {
        setManufacturerList(fleetPlanningSession.getAllManufacturer());
        setTypeFamilyList(fleetPlanningSession.getAllTypeFamily());
    }

    /**
     * Creates a new instance of FleetFilterController
     */
    public FleetFilterController() {

    }

    public List<Aircraft> getFilteredAircrafts() {
        printRetireAircraftFilters();
        List<Aircraft> filteredAircraft = fleetPlanningSession.filterAircraftsForRetire(retireAircraftFilterHelper);
        return filteredAircraft;
    }

    public List<AircraftType> getFilteredAircraftModels() {
        printAircraftModelFilters();
        List<AircraftType> filteredAircraftType = fleetPlanningSession.filterAircraftModels(aircraftModelFilterHelper);
        return filteredAircraftType;
    }

    public void setInitialValue(String filterType) {
        switch (filterType) {
            case "Retire":
                retireAircraftFilterHelper.setTimesOfMaint(0);
                retireAircraftFilterHelper.setNumOfFlightCycle(0);
                retireAircraftFilterHelper.setMaxTotalFlightDist(1000);
                retireAircraftFilterHelper.setTypeOfMaint(new ArrayList<String>());
                fleetPlanningSession.initRetireAicraftFilter(retireAircraftFilterHelper);
                break;
            case "Purchase":
                fleetPlanningSession.initAircraftModelFilter(aircraftModelFilterHelper);
                break;
        }

    }

    public void printAircraftModelFilters() {
        System.out.println("FleetFilterController: printAircraftModelFilters()");
        System.out.println("Manufacturer: " + aircraftModelFilterHelper.getManufacturers());
        System.out.println("Type Family: " + aircraftModelFilterHelper.getTypeFamilies());
        System.out.println("Max Seating: (" + aircraftModelFilterHelper.getMinMaxSeating() + ", " + aircraftModelFilterHelper.getMaxMaxSeating() + ")");
        System.out.println("Approx Price: (" + aircraftModelFilterHelper.getMinApproxPrice() + ", " + aircraftModelFilterHelper.getMaxApproxPrice() + ")");
        System.out.println("Fuel Cost: (" + aircraftModelFilterHelper.getMinFuelCostPerKm() + ", " + aircraftModelFilterHelper.getMaxFuelCostPerKm() + ")");
        System.out.println("Range: (" + aircraftModelFilterHelper.getMinRange() + ", " + aircraftModelFilterHelper.getMaxRange() + ")");
        System.out.println("Pay Load: (" + aircraftModelFilterHelper.getMinPayload() + ", " + aircraftModelFilterHelper.getMaxPayload() + ")");
        System.out.println("Max Mach Num: (" + aircraftModelFilterHelper.getMinMaxMachNum() + ", " + aircraftModelFilterHelper.getMaxMaxMachNum() + ")");
        System.out.println("Fuel Capacity: (" + aircraftModelFilterHelper.getMinFuelCapacity() + ", " + aircraftModelFilterHelper.getMaxFuelCapacity() + ")");
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

    public AircraftModelFilterHelper getAircraftModelFilterHelper() {
        return aircraftModelFilterHelper;
    }

    public void setAircraftModelFilterHelper(AircraftModelFilterHelper aircraftModelFilterHelper) {
        this.aircraftModelFilterHelper = aircraftModelFilterHelper;
    }

    /**
     * @return the manufacturerList
     */
    public List<String> getManufacturerList() {
        return manufacturerList;
    }

    /**
     * @param manufacturerList the manufacturerList to set
     */
    public void setManufacturerList(List<String> manufacturerList) {
        this.manufacturerList = manufacturerList;
    }

    /**
     * @return the typeFamilyList
     */
    public List<String> getTypeFamilyList() {
        return typeFamilyList;
    }

    /**
     * @param typeFamilyList the typeFamilyList to set
     */
    public void setTypeFamilyList(List<String> typeFamilyList) {
        this.typeFamilyList = typeFamilyList;
    }

}
