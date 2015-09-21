/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.common;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import util.security.SMTPAuthenticator;

/**
 *
 * @author winga_000
 */
@Named(value = "emailController")
@RequestScoped
public class EmailController implements Serializable{

    private String emailServerName;
    private String sender;
    private String receiver;
    private String mailer;
    
    /**
     * Creates a new instance of emailController
     */
    public EmailController() {
    }
    
    @PostConstruct
    private void init(){
        emailServerName = "mailauth.comp.nus.edu.sg";
        sender = "zweiming<zweiming@comp.nus.edu.sg>";
        mailer = "JavaMailer";
    }
    
    public void sendEmail(String subject, String mailContent, String receiver){
        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);
            if (msg != null) {
                msg.setFrom(InternetAddress.parse(sender, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver, false));
                msg.setSubject(subject);
                msg.setText(mailContent);
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
        }        
    }
    
    
    
}
