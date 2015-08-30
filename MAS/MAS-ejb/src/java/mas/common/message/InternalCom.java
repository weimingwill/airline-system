/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.message;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mas.common.entity.PlainTextMessage;

/**
 *
 * @author winga_000
 */
//@JMSDestinationDefinition(name = "jms/topicInternalCom", interfaceName = "javax.jms.Topic", resourceAdapter = "jmsra", destinationName = "topicInternalCom")
@MessageDriven(activationConfig = {
    //@ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/topicInternalCom"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/topicInternalCom"),
    //@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "durable"),
    //@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/topicInternalCom"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
})
public class InternalCom implements MessageListener {
    @Resource
    private MessageDrivenContext messageDrivenContext;
    
    @PersistenceContext(unitName = "MAS-ejbPU")
    private EntityManager em;
    
    
    
    public InternalCom() {
    }
    
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = null;
        try{
            if(message instanceof TextMessage){
                textMessage = (TextMessage) message;
                PlainTextMessage plainTextMessage = new PlainTextMessage();
                plainTextMessage.setMessage(textMessage.getText());
                em.persist(plainTextMessage);
                System.out.println("********** MessageMe: Message received and saved: " + textMessage.getText());
            } else {
                System.out.println("********** MessageMe: Message received has wrong type.");
            }
        } catch(JMSException jmsEx){
            jmsEx.printStackTrace();
            messageDrivenContext.setRollbackOnly();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
