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
    private Long systemUserId;
    private String username;
    private String password;
    private String email;
    private String resetDigest;
    private boolean locked;
    private boolean activated;
    private boolean deleted;
    private String name;
    private String address;
    private String department;
    private String phone;
    
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SystemRole> systemRoles = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.ALL} , fetch = FetchType.EAGER)
    private List<SystemMsg> systemMsgs = new ArrayList<>();
    
    public void create(String username, String password, String name, String email, 
            String phone, String address, String department){
        this.setUsername(username);
        this.setPassword(password);
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAddress(address);
        this.setDepartment(department);
    }

    public Long getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Long systemUserId) {
        this.systemUserId = systemUserId;
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
        hash += (systemUserId != null ? systemUserId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the systemUserId fields are not set
        if (!(object instanceof SystemUser)) {
            return false;
        }
        SystemUser other = (SystemUser) object;
        if ((this.systemUserId == null && other.systemUserId != null) || (this.systemUserId != null && !this.systemUserId.equals(other.systemUserId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mas.common.entity.SystemUser[ systemUserId=" + systemUserId + " ]";
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

    public List<SystemRole> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(List<SystemRole> systemRoles) {
        this.systemRoles = systemRoles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetDigest() {
        return resetDigest;
    }

    public void setResetDigest(String resetDigest) {
        this.resetDigest = resetDigest;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    
}
