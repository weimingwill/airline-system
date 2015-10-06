/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.CabinClassSessionLocal;
import ams.ais.util.exception.ExistSuchCabinClassNameException;
import ams.ais.util.exception.ExistSuchCabinClassTypeException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import java.io.Serializable;
import java.util.List;
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
public class CabinClassController implements Serializable{

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private CabinClassSessionLocal cabinClassSession;
    
    private String oldname;

    public String getOldname() {
        return oldname;
    }

    public void setOldname(String oldname) {
        this.oldname = oldname;
    }
    private String type;
    private String name;
    private CabinClass selectedCabinClass;

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
    
    public List<CabinClass> getAllCabinClass() {
        return cabinClassSession.getAllCabinClass();
    }
    
    public void deleteCabinClass() {
        try{
        cabinClassSession.deleteCabinClass(selectedCabinClass.getName());
        msgController.addMessage("Delete cabin class successfully");
        } catch (NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
          
    }
    
    public String updateCabinClass() {
        try {
            cabinClassSession.updateCabinClass(selectedCabinClass.getName(),type, name);
            msgController.addMessage("Edit cabin class successfully!");
        }catch( ExistSuchCabinClassNameException | NoSuchCabinClassException | ExistSuchCabinClassTypeException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllCabinClass();
        
    }
    
    public List<TicketFamily> getCabinClassTicketFamilys(String type){
        List<TicketFamily> ticketFamilys;
        try {
            ticketFamilys = cabinClassSession.getCabinClassTicketFamilys(type);
        } catch (NoSuchTicketFamilyException e) {
            return null;
        }
        return ticketFamilys;          
    }
    
    public List<String> getCabinClassTicketFamilyNames(String type){
        return cabinClassSession.getCabinClassTicketFamilyNames(type);
    }
    
    public CabinClass getCabinClassByName(String name){
        try {
            return cabinClassSession.getCabinClassByName(name);
        } catch (NoSuchCabinClassException ex) {
            return null;
        }
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

    public CabinClass getSelectedCabinClass() {
        return selectedCabinClass;
    }

    public void setSelectedCabinClass(CabinClass selectedCabinClass) {
        this.selectedCabinClass = selectedCabinClass;
    }
    
}
