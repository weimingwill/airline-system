/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Feedback;
import ams.crm.util.helper.FeedbackChannel;
import ams.crm.util.helper.FeedbackStatus;
import java.time.LocalDateTime;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Bowen
 */
@Stateless
public class FeedbackSession implements FeedbackSessionLocal {

    @PersistenceContext
    private EntityManager entityManager;
    
    

    @Override
    public void createFeedback(Feedback feedback) {
        Date date = new java.util.Date();
        feedback.setChannel(FeedbackChannel.INTERNET);
        feedback.setStatus(FeedbackStatus.NEW);
        feedback.setCreatedTime(date);
        entityManager.persist(feedback);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
