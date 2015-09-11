/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.security;

import javax.mail.PasswordAuthentication;

/**
 *
 * @author winga_000
 */
public class SMTPAuthenticator extends javax.mail.Authenticator {
// Replace with your actual unix id

    private static final String SMTP_AUTH_USER = "zweiming";
// Replace with your actual unix password
    private static final String SMTP_AUTH_PWD = "hongri**98";

    public SMTPAuthenticator() {
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username = SMTP_AUTH_USER;
        String password = SMTP_AUTH_PWD;
        return new PasswordAuthentication(username, password);
    }    
}
