/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.Feedback;
import ams.crm.session.FeedbackSessionLocal;

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
@Named(value = "feedbackController")
@RequestScoped
public class FeedbackController {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    @EJB
    private FeedbackSessionLocal feedbackSession;
    
    private Feedback newFeedback=new Feedback();

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
       
            feedbackSession.createFeedback(newFeedback);
            msgController.addMessage("Feedback sent successfully!");
       
        return navigationController.redirectToCurrentPage();
    }
    
    public FeedbackController() {
    }
    
}
