/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Customer;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface CustomerSessionLocal {

    public List<Customer> getAllCustomers();

    public List<Customer> searchCustomer(String passportNo, String email, String firstName, String lastName, String membershipId);

    public List<Customer> searchCustomerByFirstName(String firstName);

    public List<Customer> searchCustomerByLastName(String lastName);
    
    public List<Customer> searchCusotmerByPassportNo(String passportNo);

}
