/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm.entity;

import ams.ars_crm.entity.helper.PrivilegeValueId;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author weiming
 */
@Entity
public class PrivilegeValue implements Serializable {

    @EmbeddedId
    private PrivilegeValueId privilegeValueId;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "MEMBERSHIP", referencedColumnName = "ID")
    private Membership membership;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "PRIVILEGEID", referencedColumnName = "ID")
    private Privilege privilege;

    @Column(name = "PRIVILEGEVALUE")
    private Double privilegeValue;

    public PrivilegeValueId getPrivilegeValueId() {
        return privilegeValueId;
    }

    public void setPrivilegeValueId(PrivilegeValueId privilegeValueId) {
        this.privilegeValueId = privilegeValueId;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public Double getPrivilegeValue() {
        return privilegeValue;
    }

    public void setPrivilegeValue(Double privilegeValue) {
        this.privilegeValue = privilegeValue;
    }

}
