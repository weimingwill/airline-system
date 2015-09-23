/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import ams.aps.session.RoutePlanningSession;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;

/**
 *
 * @author ChuningLiu
 */
@Named(value = "routePlanningBean")
@SessionScoped
public class RoutePlanningController implements Serializable {

    @EJB
    private RoutePlanningSession routePlanningSession;
    /**
     * Creates a new instance of RoutePlanningBean
     */
    public RoutePlanningController() {
    }
    
    
}
