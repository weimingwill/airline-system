/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Feedback;
import ams.crm.entity.RegCust;
import ams.crm.util.helper.FeedbackChannel;
import ams.crm.util.helper.FeedbackStatus;
import java.util.Date;
import java.util.List;
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
    public void createFeedback(Feedback feedback, RegCust regCust) {
        Date date = new java.util.Date();

        feedback.setChannel(FeedbackChannel.INTERNET);
        feedback.setStatus(FeedbackStatus.NEW);
        feedback.setCreatedTime(date);
        entityManager.persist(feedback);
        System.out.println("regcustid is "+regCust.getId());
        regCust = entityManager.find(RegCust.class, regCust.getId());
        List<Feedback> feedbackList = regCust.getFeedbacks();
        System.out.println("feedbacks" +regCust.getFeedbacks());
        feedbackList.add(feedback);
        regCust.setFeedbacks(feedbackList);
        entityManager.merge(regCust);
        entityManager.flush();

    }
    
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
