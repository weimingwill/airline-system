/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.ais;

import ams.ais.session.BookingClassSessionLocal;
import ams.ais.util.exception.ExistSuchBookingClassNameException;
import ams.ais.util.exception.NoSuchBookingClassException;
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
 * @author Tongtong
 */
@Named(value = "bookingClassController")
@RequestScoped
public class BookingClassController {

    @Inject
    private MsgController msgController;
    @Inject
    private NavigationController navigationController;
    
    
    @EJB
    private BookingClassSessionLocal bookingClassSession;
    
    private String bookingClassName;
    /**
     * Creates a new instance of BookingClassController
     */
    public BookingClassController() {
    }
    
    public String createBookingClass(){
        try {
            bookingClassSession.createBookingClass(bookingClassName);
            msgController.addMessage("Create booking class successfully!");
        } catch (ExistSuchBookingClassNameException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCreateBookingClass();
    }
    
    public String deleteBookingClass(){
        try {
            bookingClassSession.deleteBookingClass(bookingClassName);
            msgController.addMessage("Booking class is deleted successfully!");
        } catch (NoSuchBookingClassException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToDeleteBookingClass(); 
    }

    
    //Getter and Setter
    public String getBookingClassName() {
        return bookingClassName;
    }

    public void setBookingClassName(String bookingClassName) {
        this.bookingClassName = bookingClassName;
    }
    
    
}
