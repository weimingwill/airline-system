/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.entity.BookingClass;
import ams.ais.entity.CabinClass;
import ams.ais.entity.TicketFamily;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.ExistSuchTicketFamilyException;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.exception.NoSuchCabinClassException;
import ams.ais.util.exception.NoSuchTicketFamilyException;
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
    private String oldtype;
    private String oldCabinClassname;
    private String type;
    private String name;
    private String cabinclassname;
    private List<String> bookingClassNames;
    
    public TicketFamilyController() {
    }
    
    public String createTicketFamily() {
        try {
            ticketFamilySession.createTicketFamily(type, name,cabinclassname);
            msgController.addMessage("Create ticket family successfully!");
        } catch (ExistSuchTicketFamilyException | NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateTicketFamily();
    }
    
    public List<TicketFamily> getAllTicketFamily() {
        return ticketFamilySession.getAllTicketFamily();
    }
    
    public List<CabinClass> getAllCabinClass() {
        return ticketFamilySession.getAllCabinClass();
        
    }
    public void deleteTicketFamily() {
        try{
        ticketFamilySession.deleteTicketFamily(type,cabinclassname);
        msgController.addMessage("Delete ticket family successfully");
        } catch (NoSuchTicketFamilyException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
          
    }
    public String updateTicketFamily() {
        try {
            ticketFamilySession.updateTicketFamily(oldtype,oldCabinClassname,type,name,cabinclassname);
            msgController.addMessage("Edit ticket family successfully!");
        }catch( ExistSuchTicketFamilyException | NoSuchTicketFamilyException | NoSuchCabinClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewAllTicketFamily();
        
    }
    
    public List<BookingClass> getTicketFamilyBookingClasses(String cabinClassName, String ticketFamilyName){
        List<BookingClass>  bookingClasses;
        try {
            bookingClasses = ticketFamilySession.getTicketFamilyBookingClasses(cabinClassName, ticketFamilyName);
        } catch (NoSuchBookingClassException e) {
            return null;
        }
        return bookingClasses;
    }
    
    public List<String> getTicketFamilyBookingClassNames(String cabinClassName, String ticketFamilyName){
        return ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassName, ticketFamilyName);
    }
    
    public void OnTicketFamilyChange(String cabinClassName, String ticketFamilyName){
        bookingClassNames = ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassName, ticketFamilyName);
    }
//    public List<BookingClass> getTicketFamilyBookingClasses(Long cabinClassId, String type){
//        List<BookingClass>  bookingClasses;
//        try {
//            bookingClasses = ticketFamilySession.getTicketFamilyBookingClasses(cabinClassId, type);
//        } catch (NoSuchBookingClassException e) {
//            return null;
//        }
//        return bookingClasses;
//    }
//    
//    public List<String> getTicketFamilyBookingClassNames(Long cabinClassId, String type){
//        return ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassId, type);
//    }
//    
//    public void OnTicketFamilyChange(Long cabinClassId, String name){
//        bookingClassNames = ticketFamilySession.getTicketFamilyBookingClassNames(cabinClassId, name);
//    }
   
    
    //Getter and setter

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    public String getOldtype() {
        return oldtype;
    }

    public void setOldtype(String oldtype) {
        this.oldtype = oldtype;
    }

    public String getOldCabinClassname() {
        return oldCabinClassname;
    }

    public void setOldCabinClassname(String oldCabinClassname) {
        this.oldCabinClassname = oldCabinClassname;
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

    public String getCabinclassname() {
        return cabinclassname;
    }

    public void setCabinclassname(String cabinclassname) {
        this.cabinclassname = cabinclassname;
    }

    public List<String> getBookingClassNames() {
        return bookingClassNames;
    }

    public void setBookingClassNames(List<String> bookingClassNames) {
        this.bookingClassNames = bookingClassNames;
    }


}
