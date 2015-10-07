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
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
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
import mas.common.util.helper.SystemMsgHelper;

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

    public String saveMessage(String subject, String message, String[] receivers) {
        String messageContent = String.valueOf(message);
        String messageSubject = String.valueOf(subject);
        try {
            sendMessage(messageSubject, messageContent, receivers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return navigationController.redirectToSendMessages();
    }

    public void print() {
        System.out.println("print");
    }

    //send message
    public void sendMessage(String subject, String message, String[] receivers) {
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
                mapMessage.setString("subject", subject);
                mapMessage.setString("message", message);
                mapMessage.setString("sender", username);
                mapMessage.setInt("receiverNumber", receivers.length);
                for (int i = 0; i < receivers.length; i++) {
                    mapMessage.setString("receiver" + i, receivers[i]);
                }
                messageProducer.send(mapMessage);
                msgController.addMessage("Your message: " + message + " have been sent");
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

    public List<SystemMsgHelper> getSystemMsgHelpers() {
        return systemUserSession.getSystemMsgHelpers(username);
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

    public void flagMessage(Long messageId) {
        try {
            systemUserSession.flagMessage(username, messageId);
            msgController.addMessage("Flag message successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }

    public void unFlagMessage(Long messageId) {
        try {
            systemUserSession.unFlagMessage(username, messageId);
            msgController.addMessage("Unflag message " + message + " successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
    }
    
    public String deleteMessage(Long messageId) {
        try {
            systemUserSession.deleteMessage(username, messageId);
            msgController.addMessage("Delete message successfully!");
        } catch (NoSuchMessageException ex) {
            msgController.addErrorMessage(ex.getMessage());
        }
        return navigationController.redirectToViewMessages();
    }

    public String getMessageTableRowClasses() {
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
