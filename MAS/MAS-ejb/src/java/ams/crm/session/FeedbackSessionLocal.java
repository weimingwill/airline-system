/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Feedback;
import javax.ejb.Local;

/**
 *
 * @author Bowen
 */
@Local
public interface FeedbackSessionLocal {
    public void createFeedback(Feedback feedback);
}
