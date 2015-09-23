/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.TicketFamily;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchTicketFamilyNameException;
import ams.ais.util.exception.ExistSuchTicketFamilyTypeException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
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
@Named(value = "ticketFamilyController")
@RequestScoped
public class TicketFamilyController {

    /**
     * Creates a new instance of TicketFamilyController
     */
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    private String oldname;
    private String type;
    private String name;

    public String getOldname() {
        return oldname;
    }

    public void setOldname(String oldname) {
        this.oldname = oldname;
    }

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
    
    
    public TicketFamilyController() {
    }
    
    public String createTicketFamily(){
        try {
            ticketFamilySession.createTicketFamily(type, name);
            msgController.addMessage("Create ticket family successfully!");
        } catch (ExistSuchTicketFamilyNameException | ExistSuchTicketFamilyTypeException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateTicketFamily();
    }
    
    public List<TicketFamily> getAllTicketFamily() {
        return ticketFamilySession.getAllTicketFamily();
    }
    
    public void deleteTicketFamily() {
        try{
        ticketFamilySession.deleteTicketFamily(name);
        msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
          
    }
    public String updateTicketFamily() {
        try {
            ticketFamilySession.updateTicketFamily(oldname,type, name);
            msgController.addMessage("Edit ticket family successfully!");
        }catch( ExistSuchTicketFamilyNameException | NoSuchTicketFamilyException | ExistSuchTicketFamilyTypeException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();
        
    }
}
