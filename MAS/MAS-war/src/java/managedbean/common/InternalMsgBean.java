/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
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
import mas.common.entity.SystemMsg;
import mas.common.session.InternalMsgSessionLocal;
import mas.common.session.SystemUserSessionLocal;

/**
 *
 * @author winga_000
 */
@Named(value = "internalMsgBean")
@SessionScoped
public class InternalMsgBean implements Serializable {

    @EJB
    private InternalMsgSessionLocal internalMsgSession;
    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String message;
    private String receiver;
    private String username;
    private String[] receivers;

    @Inject
    private LoginBean loginBean;

    //Initialization
    public InternalMsgBean() {
        this.message = "Initialize message";
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.username = (String) sessionMap.get("username");
//        this.username = loginBean.getUsername();
    }

    public List<SystemMsg> getAllMessages() {
        return internalMsgSession.getAllInternalMessages();
    }

    public List<SystemMsg> getUserMessages() {
        return systemUserSession.getUserMessages(username);
    }

    public void saveMessage() {
        String messageContent = String.valueOf(message);
        try {
            sendMessage(messageContent, receivers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                mapMessage.setInt("receiverNumber", receivers.length);
                for (int i = 0; i < receivers.length; i++) {
                    mapMessage.setString("receiver" + i, receivers[i]);
                }
                messageProducer.send(mapMessage);
                FacesContext context = FacesContext.getCurrentInstance();
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

    public List<SystemMsg> getUneadMessages() {
        try {
            return systemUserSession.getUserMessages(username);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public int getUnreadMessagesNumber() {
        try {
            return systemUserSession.getUserUnreadMessages(username).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void readUnreadMessages(ActionEvent event) {
        systemUserSession.readUnreadMessages(username);
    }

    //Getter and Setter
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

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

}
