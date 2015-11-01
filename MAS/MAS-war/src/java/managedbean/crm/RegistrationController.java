/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.RegCust;
import ams.crm.entity.helper.Phone;
import ams.crm.session.CustomerSessionLocal;
import ams.crm.util.exception.ExistSuchRegCustException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import managedbean.common.EmailController;

/**
 *
 * @author Bowen
 */
@Named(value = "registrationController")
@RequestScoped
public class RegistrationController implements Serializable{
    @Inject
    private MsgController msgController;
    @Inject
    private NavigationController navigationController;
    @Inject
    private EmailController emailController;
    @EJB
    private CustomerSessionLocal registrationSession;
    /**
     * Creates a new instance of RegistrationController
     */
    
    private RegCust newRegCust;
   
  
    @PostConstruct
    public void init(){
        newRegCust = new RegCust();
        newRegCust.setPhone(new Phone());
    }
    public String createrRegCust(){
        try {
           
            
            registrationSession.createRegCust(newRegCust);
            String subject = "Welcome to Merlion Air";
            String mailContent = "Dear Customer: \n Please activate your account using the following link: \n" + navigationController.redirectToCustomerLogin() + "resetPassword.xhtml?faces-redirect=true&resetDigest=" + "&email=" + newRegCust.getEmail();
            String receiver = newRegCust.getEmail();
            emailController.sendEmail(subject, mailContent, receiver);
           
//            registrationSession.createRegCust(newRegCust.getTitle(),newRegCust.getFirstName(),newRegCust.getLastName(),newRegCust.getPassportNo(),newRegCust.getNationality(),newRegCust.getGender(),newRegCust.getDob(),newRegCust.getEmail(),newRegCust.getAddr1(),newRegCust.getAddr2(),newRegCust.getCity(),newRegCust.getProvince(),newRegCust.getCountry(),newRegCust.getZipCode(),newRegCust.getMobilephone(),newRegCust.getTelephone(),newRegCust.getPwd(),newRegCust.getSecurQuest(),newRegCust.getSecurAns(),newRegCust.getNewsLetterPref(),newRegCust.getPromoPref(),membershipClass,accMiles,custValue,numOfFlights, memberShipId);
            msgController.addMessage("Registrated successfully!");
            
        } catch (ExistSuchRegCustException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToCurrentPage();
    }

    
    
    public RegCust getNewRegCust() {
        return newRegCust;
    }

    public void setNewRegCust(RegCust newRegCust) {
        this.newRegCust = newRegCust;
    }
    
   
    
    public RegistrationController() {
    }
    
}
