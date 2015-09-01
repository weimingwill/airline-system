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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mas.common.entity.PlainTextMessage;
import mas.common.session.InternalComSessionBean;

/**
 *
 * @author winga_000
 */
//@ManagedBean
@Named(value = "internalComManagedBean")
@RequestScoped
public class InternalComManagedBean {

    @EJB
    private InternalComSessionBean internalComSessionBean;
    private String message;
    
    public InternalComManagedBean() {
        message = "Initialize message";
    }

    public InternalComSessionBean getInternalComSessionBean() {
        return internalComSessionBean;
    }

    public void setInternalComSessionBean(InternalComSessionBean internalComSessionBean) {
        this.internalComSessionBean = internalComSessionBean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<PlainTextMessage> getAllMessages(){
        return internalComSessionBean.getAllPlainTextMessage();
    }
    
    public void saveNewMessage(ActionEvent event) {
        String messageContent = String.valueOf(message);
        try {
            sendMessage(messageContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    public void sendMessage(String message) {
        try {
            Context c = new InitialContext();
            ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/topicInternalComConnectionFactory");
            Connection connection = null;
            Session session = null;

            try {
                connection = cf.createConnection();
                session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
                Destination destination = (Destination) c.lookup("jms/topicInternalCom");
                MessageProducer messageProducer = session.createProducer(destination);
                messageProducer.send(session.createTextMessage(message));
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
