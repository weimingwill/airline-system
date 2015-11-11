/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.Feedback;
import ams.crm.session.FeedbackSessionLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "feedbackBacking")
@ViewScoped
public class FeedbackBacking implements Serializable{
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
    
    @EJB
    private FeedbackSessionLocal feedbackSession;
    /**
     * Creates a new instance of FeedbackBacking
     */
    private Feedback selectFeedback;

    public Feedback getSelectFeedback() {
        return selectFeedback;
    }

    public void setSelectFeedback(Feedback selectFeedback) {
        this.selectFeedback = selectFeedback;
    }
    
    public FeedbackBacking() {
    }
    
   
          
    
    
}
