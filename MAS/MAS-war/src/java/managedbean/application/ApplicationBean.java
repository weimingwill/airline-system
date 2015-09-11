/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;

/**
 *
 * @author winga_000
 */
@Named(value = "applicationBean")
@RequestScoped
public class ApplicationBean {

    @EJB
    private SystemUserSessionLocal systemUserSession;
    private String username;
    private List<String> otherUsernames;
    
    public ApplicationBean() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();        
        this.username = (String) sessionMap.get("username");    
    }
    
    @PostConstruct
    public void init() {
        otherUsernames = new ArrayList<String>();
        try {
            for (Object obj : getAllOtherUsers()){
                SystemUser user = (SystemUser)obj;
                otherUsernames.add(user.getUsername());
            }
        } catch (NullPointerException e) {
        }
    }    
    
    public List<SystemUser> getAllUsers(){
        return systemUserSession.getAllUsers();
    }
    
    public List<SystemUser> getAllOtherUsers(){
        return  systemUserSession.getAllOtherUsers(username);
    }

    public List<String> getOtherUsernames() {
        return otherUsernames;
    }

    public void setOtherUsernames(List<String> otherUsernames) {
        this.otherUsernames = otherUsernames;
    }
    
    
}
