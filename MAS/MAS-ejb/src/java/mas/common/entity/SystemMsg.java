/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.entity;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winga_000
 */
@Entity
public class SystemMsg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long systemMsgId;
    private String message;
    private String messageFrom;
    private Boolean readed;
    private Boolean flaged;
    private Boolean deleted;
    @Temporal(value = TemporalType.DATE)
    private Date messageTime;
    private String subject;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "systemMsgs")
    private List<SystemUser> systemUsers = new ArrayList<>();

    public void create(String message, String messageFrom) {
        this.setMessage(message);
        this.setMessageFrom(messageFrom);
        this.setReaded(false);
        this.setFlaged(false);
        this.setDeleted(false);
    }

    public Boolean isReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }

    public Long getSystemMsgId() {
        return systemMsgId;
    }

    public void setSystemMsgId(Long systemMsgId) {
        this.systemMsgId = systemMsgId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SystemUser> getSystemUsers() {
        return systemUsers;
    }

    public void setSystemUsers(List<SystemUser> systemUsers) {
        this.systemUsers = systemUsers;
    }

    public boolean isFlaged() {
        return flaged;
    }

    public void setFlaged(Boolean flaged) {
        this.flaged = flaged;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (systemMsgId != null ? systemMsgId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SystemMsg)) {
            return false;
        }
        SystemMsg other = (SystemMsg) object;
        if ((this.systemMsgId == null && other.systemMsgId != null) || (this.systemMsgId != null && !this.systemMsgId.equals(other.systemMsgId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mas.common.entity.InternalMessage[ systemMsgId=" + systemMsgId + " ]";
    }

}
