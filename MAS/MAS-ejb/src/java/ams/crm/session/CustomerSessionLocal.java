/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.crm.entity.Customer;
import ams.crm.entity.CustomerList;
import ams.crm.entity.Feedback;
import ams.crm.entity.RegCust;
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

//    public List<Customer> searchCustomerByFirstName(String firstName);
//
//    public List<Customer> searchCustomerByLastName(String lastName);
//    
//    public List<Customer> searchCusotmerByPassportNo(String passportNo);
    
    public RegCust findRegCustByCustomer(Customer customer);
    
    public Feedback createFeedback(RegCust regCust, String subject, String content, String channel);
    
    public List<Feedback> getRegCustFeedback(RegCust regCust);
    
    public List<Customer> getCustomerByPassportNo(String passportNo);
    
    public List<Customer> getCustomerByName(String firstName, String lastName);
    
    public List<Customer> getCustomerByMembershipId(String membershipId);
    
    public List<String> getAllNationalities();
    
    public List<RegCust> filterCustomer(String gender, String nationality, int minAge, int maxAge);
    
    public CustomerList createCustomerList(List<RegCust> customers, String name);
    
    public List<RegCust> getAllRegCusts();
    
    public List<CustomerList> getAllCustomerLists();
    

}
