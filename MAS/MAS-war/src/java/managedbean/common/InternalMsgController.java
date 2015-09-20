/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
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
import managedbean.application.MsgController;
import managedbean.application.NavigationController;
import mas.common.entity.SystemMsg;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchMessageException;

/**
 *
 * @author winga_000
 */
@Named(value = "internalMsgController")
@RequestScoped
public class InternalMsgController implements Serializable {

    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String message;
    private String receiver;
    private String username;
    private String[] receivers;

    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;

    //Initialization
    public InternalMsgController() {   
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
    }    
    
    public String saveMessage(String message, String[] receivers) {
        String messageContent = String.valueOf(message);
        try {
            sendMessage(messageContent, receivers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return navigationController.redirectToSendMessages();
    }

    //send message
    public void sendMessage(String message, String[] receivers) {
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
                mapMessage.setString("sender", username);
                mapMessage.setInt("receiverNumber", receivers.length);
                for (int i = 0; i < receivers.length; i++) {
                    mapMessage.setString("receiver" + i, receivers[i]);
                }
                messageProducer.send(mapMessage);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null, new FacesMessage("Successful", "Your message: " + message + " have been sent"));
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

    public List<SystemMsg> getUserMessages() {
        return systemUserSession.getUserMessages(username);
    }
    
    public List<SystemMsg> getUnreadMessages() {
        return systemUserSession.getUserMessages(username);
    }

    public int getUnreadMessagesNumber() {
        List<SystemMsg> systemMsgs = systemUserSession.getUserUnreadMessages(username);
        if (systemMsgs == null) {
            return 0;
        }
        return systemMsgs.size();
    }

    public void readUnreadMessages(ActionEvent event) {
        try {
            systemUserSession.readUnreadMessages(username);
        } catch (NoSuchMessageException e) {
        }
    }
    
    public void flagMessage(String message){
        try {
            systemUserSession.flagMessage(username, message);
            msgController.addMessage("Flag message " + message + " successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }
    
    public void unFlagMessage(String message){
        try {
            systemUserSession.unFlagMessage(username, message);
            msgController.addMessage("Unflag message " + message + " successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }
    
    public void deleteMessage(String message){
        try {
            systemUserSession.deleteMessage(username, message);
            msgController.addMessage("Delete message " + message + " successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public String getMessageTableRowClasses(){
        StringBuilder sb = new StringBuilder();
        for (SystemMsg msg : getUserMessages()) {
            sb.append((msg.isDeleted()) ? "hide," : "show,");
        }
        return sb.toString();
    }
    
    //Getter and Setter
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

    public SystemUserSessionLocal getSystemUserSession() {
        return systemUserSession;
    }

    public void setSystemUserSession(SystemUserSessionLocal systemUserSession) {
        this.systemUserSession = systemUserSession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

}
