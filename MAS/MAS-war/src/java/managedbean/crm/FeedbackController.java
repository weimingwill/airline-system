/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.Feedback;
import ams.crm.entity.RegCust;
import ams.crm.session.CustomerExSessionLocal;
import ams.crm.session.FeedbackSessionLocal;
import ams.crm.util.exception.NoSuchRegCustException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "feedbackController")
@RequestScoped
public class FeedbackController implements Serializable {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private FeedbackSessionLocal feedbackSession;
    @EJB
    private CustomerExSessionLocal customerSession; 
    
    private Feedback newFeedback=new Feedback();
    private String email;
    private RegCust regCust;
    private Feedback selectFeedback;

    public Feedback getSelectFeedback() {
        return selectFeedback;
    }

    public void setSelectFeedback(Feedback selectFeedback) {
        this.selectFeedback = selectFeedback;
    }

    public RegCust getRegCust() {
        return regCust;
    }

    public void setRegCust(RegCust regCust) {
        this.regCust = regCust;
    }

    
    
    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.email = (String) sessionMap.get("email");
        initializeCustomer();
    }
    
    public void initializeCustomer(){
        try {
            regCust = getRegCustByEmail();
        } catch (NoSuchRegCustException ex) {
            email=null;
        }
    }
    
    public RegCust getRegCustByEmail() throws NoSuchRegCustException{
       return  customerSession.getRegCustByEmail(email);  
    }
    public Feedback getNewFeedback() {
        return newFeedback;
    }

    public void setNewFeedback(Feedback newFeedback) {
        this.newFeedback = newFeedback;
    }
    /**
     * Creates a new instance of FeedbackController
     * @return 
     */
    
    public String createrFeedback(){
       
            feedbackSession.createFeedback(newFeedback,regCust);
            msgController.addMessage("Feedback sent successfully!");
       
        return navigationController.redirectToCurrentPage();
    }
    
    public FeedbackController() {
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
