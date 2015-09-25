/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.entity.FlightSchedule;
import ams.aps.session.FlightScheduleSessionLocal;
import ams.aps.util.exception.NoSuchFlightSchedulException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author winga_000
 */
@Named(value = "flightScheduleController")
@RequestScoped
public class FlightScheduleController {
    
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private FlightScheduleSessionLocal flightScheduleSession;
    /**
     * Creates a new instance of FlightSchedulingController
     */
    public FlightScheduleController() {
    }
    
    public List<FlightSchedule> getAllFlightSchedule(){
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = flightScheduleSession.getAllFilghtSchedules();
        } catch (NoSuchFlightSchedulException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return flightSchedules;
    }
    
    
}
