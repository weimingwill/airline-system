/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author winga_000
 */
@Entity
public class PlainTextMessage implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plainTextMessageId;
    private String message;

    public Long getPlainTextMessageId() {
        return plainTextMessageId;
    }

    public void setPlainTextMessageId(Long plainTextMessageId) {
        this.plainTextMessageId = plainTextMessageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (plainTextMessageId != null ? plainTextMessageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlainTextMessage)) {
            return false;
        }
        PlainTextMessage other = (PlainTextMessage) object;
        if ((this.plainTextMessageId == null && other.plainTextMessageId != null) || (this.plainTextMessageId != null && !this.plainTextMessageId.equals(other.plainTextMessageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mas.common.entity.PlainTextMessage[ plainTextMessageId=" + plainTextMessageId + " ]";
    }
    
}
