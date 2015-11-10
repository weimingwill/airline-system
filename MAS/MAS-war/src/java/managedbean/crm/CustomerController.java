/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.ais.entity.CabinClass;
import ams.ais.util.helper.FlightSchCabinClsTicFamBookingClsHelper;
import ams.aps.util.helper.ApsMessage;
import ams.crm.entity.Customer;
import ams.crm.entity.RegCust;
import ams.crm.session.CustomerSessionLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.CrmNavController;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Tongtong
 */
@Named(value = "customerController")
@RequestScoped
public class CustomerController implements Serializable {

    @Inject
    private CrmNavController crmNavController;
    @Inject
    private MsgController msgController;
    @EJB
    private CustomerSessionLocal customerSession;

    private Customer customer;
    private Customer selectedCustomer;
    private List<Customer> customers;
    private RegCust regCustomer;
    private RegCust selectedRegCust;
    private String passportNo;
    private String firstName;
    private String lastName;
    private String email;
    private String membershipId;

    /**
     * Creates a new instance of CustomerController
     */
    @PostConstruct
    public void init() {
        customers = getAllCustomers();
    }

    public CustomerController() {
    }

    public String toViewFeedbacks() {
        if (selectedCustomer != null) {
            return crmNavController.redirectToViewFeedbacks();
        }
        msgController.addErrorMessage("Please select an customer");
        return "";
    }

    public String toCreateFeedback() {
        if (selectedCustomer != null) {
            return crmNavController.redirectToCreateFeedback();
        }
        msgController.addErrorMessage("Please select an customer");
        return "";
    }

    public void searchCustomer() {
        System.out.println("start to search customer");
        customers = customerSession.searchCustomer(passportNo, email, firstName, lastName, membershipId);
        System.out.println(customers.size() + " results found");

    }

    public void resetSearchCustomer() {
        customers = getAllCustomers();
    }
    
    public void searchCustomerByFirstName(String firstName){
        customers = customerSession.searchCustomerByFirstName(firstName);      
    }
    
    public void searchCustomerByLastName(String familyName){
        customers = customerSession.searchCustomerByFirstName(familyName);      
    }
    
    public void searchCustomerByPassportNumber(String passportNumber){
        customers = customerSession.searchCustomerByFirstName(passportNumber);     
    }

    //*******************getters & setters************************
    public List<Customer> getAllCustomers() {
        return customerSession.getAllCustomers();
    }

    public CrmNavController getCrmNavController() {
        return crmNavController;
    }

    public void setCrmNavController(CrmNavController crmNavController) {
        this.crmNavController = crmNavController;
    }

    public MsgController getMsgController() {
        return msgController;
    }

    public void setMsgController(MsgController msgController) {
        this.msgController = msgController;
    }

    public CustomerSessionLocal getCustomerSession() {
        return customerSession;
    }

    public void setCustomerSession(CustomerSessionLocal customerSession) {
        this.customerSession = customerSession;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public RegCust getRegCustomer() {
        return regCustomer;
    }

    public void setRegCustomer(RegCust regCustomer) {
        this.regCustomer = regCustomer;
    }

    public RegCust getSelectedRegCust() {
        return selectedRegCust;
    }

    public void setSelectedRegCust(RegCust selectedRegCust) {
        this.selectedRegCust = selectedRegCust;
    }

}
