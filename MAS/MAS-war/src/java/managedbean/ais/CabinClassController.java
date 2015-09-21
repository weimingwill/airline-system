/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.session.CabinClassSessionLocal;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "cabinClassController")
@RequestScoped
public class CabinClassController {

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private CabinClassSessionLocal cabinClassSession;
    
    private String type;
    private String name;
    /**
     * Creates a new instance of CabinClassController
     */
    public CabinClassController() {
    }

    public String createCabinClass(){
        try {
            cabinClassSession.createCabinClass(type, name);
            msgController.addMessage("Create cabin class successfully!");
        } catch (ExistSuchCabinClassNameException | ExistSuchCabinClassTypeException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateCabinClass();
    }
    
    //Getter and Setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
    
    
}
