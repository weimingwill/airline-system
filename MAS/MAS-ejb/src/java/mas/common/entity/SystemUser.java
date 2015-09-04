/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author Lewis
 */
@Entity
public class SystemUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SystemRole> roles = new ArrayList<SystemRole>();
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SystemMsg> systemMsgs = new ArrayList<SystemMsg>();
    
    public void create(String username, String password){
        this.setUsername(username);
        this.setPassword(password);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SystemMsg> getSystemMsgs() {
        return systemMsgs;
    }

    public void setSystemMsgs(List<SystemMsg> systemMsgs) {
        this.systemMsgs = systemMsgs;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SystemUser)) {
            return false;
        }
        SystemUser other = (SystemUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mas.common.entity.SystemUser[ id=" + id + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<SystemRole> roles) {
        this.roles = roles;
    }

    /**
     * @return the roles
     */
    public List<SystemRole> getRoles() {
        return roles;
    }
    
}
