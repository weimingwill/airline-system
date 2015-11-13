/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

/**
 *
 * @author Tongtong
 */
@Entity
public class SelectedCust extends RegCust implements Serializable {
    
    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy = "selectedCusts")
    private List<CustomerList> customerLists;

    public List<CustomerList> getCustomerLists() {
        return customerLists;
    }

    public void setCustomerLists(List<CustomerList> customerLists) {
        this.customerLists = customerLists;
    }
}
