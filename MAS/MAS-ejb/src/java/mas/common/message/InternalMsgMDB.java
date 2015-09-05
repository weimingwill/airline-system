/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.message;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mas.common.entity.SystemMsg;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;

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
public class InternalMsgMDB implements MessageListener {
    @Resource
    private MessageDrivenContext messageDrivenContext;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @EJB
    private SystemUserSessionLocal systemUserSession;
    private SystemMsg systemMsg;
    private SystemUser systemUser;
    
    public InternalMsgMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = null;
        try{
            if(message instanceof MapMessage){
                
                mapMessage = (MapMessage) message;
                String messageContent = mapMessage.getString("message");
                systemMsg = new SystemMsg();
                systemMsg.create(messageContent);//create a new systemMsg record
                
                List<String> receivers = new ArrayList<String>();
                int receiverNumber = mapMessage.getInt("receiverNumber");
                for(int i = 0; i < receiverNumber; i++){
                    String receiver = mapMessage.getString("receiver" + i);
                    receivers.add(receiver);
                    systemUser = systemUserSession.getSystemUserByName(receiver);
                    systemUser.getSystemMsgs().add(systemMsg);
                    systemMsg.getSystemUsers().add(systemUser);  
                    entityManager.persist(systemUser);                
                }
                System.out.println("********** InternalMsgMDB: Message " + messageContent + " send to : " + receivers);
            } else {
                System.out.println("********** InternalMsgMDB: Message received has wrong type.");
            }
        } catch(JMSException jmsEx){
            jmsEx.printStackTrace();
            messageDrivenContext.setRollbackOnly();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
