/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import java.util.List;
import javax.ejb.EJB;
//import javax.faces.bean.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mas.common.entity.SystemMsg;
import mas.common.session.InternalMsgSessionLocal;
import mas.common.session.SystemUserSessionLocal;

/**
 *
 * @author winga_000
 */
//@ManagedBean
@Named(value = "internalMsgBean")
@RequestScoped
public class InternalMsgBean {

    @EJB
    private InternalMsgSessionLocal internalMsgSession;
    private SystemUserSessionLocal systemUserSession;
    private LoginBean loginManagedBean;
    private String message;
    private String receiver;
    private String username;
    
    public void getUsername(){
        username = loginManagedBean.getUsername();
    }
    
    public InternalMsgBean() {
        message = "Initialize message";
        receiver = "user1";
    }

    public InternalMsgSessionLocal getInternalMsgSession() {
        return internalMsgSession;
    }

    public void setInternalMsgSession(InternalMsgSessionLocal internalMsgSession) {
        this.internalMsgSession = internalMsgSession;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    
    public List<SystemMsg> getAllMessages(){
        return internalMsgSession.getAllInternalMessages();
    }
    
    public List<SystemMsg> getUserMessages(String username){
        return systemUserSession.getUserMessages(username);
    }
    
    public void saveNewMessage(ActionEvent event) {
        String messageContent = String.valueOf(message);
        String receiverContent = String.valueOf(receiver);
        try {
            sendMessage(messageContent, receiverContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    //send message
    public void sendMessage(String message, String receiver) {
        try {
            Context c = new InitialContext();
            ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/topicInternalComConnectionFactory");
            Connection connection = null;
            Session session = null;
            MapMessage mapMessage = null;
            Destination destination = null;
            MessageProducer messageProducer = null;
            
            try {
                connection = cf.createConnection();
                session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
                destination = (Destination) c.lookup("jms/topicInternalCom");
                messageProducer = session.createProducer(destination);
                mapMessage = session.createMapMessage();
                mapMessage.setString("message", message);
                mapMessage.setString("receiver", receiver);
                messageProducer.send(mapMessage);
            } catch (JMSException jmsEx) {
                jmsEx.printStackTrace();
            } finally {
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (NamingException namingEx) {
            namingEx.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
