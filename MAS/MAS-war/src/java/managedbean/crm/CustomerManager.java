/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.Customer;
import ams.crm.entity.CustomerList;
import ams.crm.entity.Feedback;
import ams.crm.entity.RegCust;
import ams.crm.session.CustomerSessionLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import managedbean.application.CrmNavController;
import managedbean.application.MsgController;

/**
 *
 * @author Tongtong
 */
@Named(value = "customerManager")
@SessionScoped
public class CustomerManager implements Serializable {

    @Inject
    private CrmNavController crmNavController;
    @Inject
    private MsgController msgController;
    @EJB
    private CustomerSessionLocal customerSession;

    private Customer customer;
    private Customer selectedCustomer;
    private List<Customer> customers;
    private List<RegCust> regCusts;
    private RegCust regCustomer;
    private RegCust selectedRegCust;
    private String passportNo;
    private String firstName;
    private String lastName;
    private String email;
    private String membershipId;
    private List<String> predefinedChannels;
    private String feedbackSubject;
    private String feedbackContent;
    private String feedbackChannel;
    private List<Feedback> feedbacks;
    private int minAge;
    private int maxAge;
    private String dtype;
    private String gender;
    private List<String> nationalities;
    private String nationality;
    private List<RegCust> filteredRegCusts;
    private CustomerList customerList;
    private List<CustomerList> customerLists;
    private String customerListId;

    /**
     * Creates a new instance of customerManager
     */
    @PostConstruct
    public void init() {
        customers = getAllCustomers();
        regCusts = customerSession.getAllRegCusts();
        List<String> channels = new ArrayList<>();
        channels.add("Call center");
        channels.add("Customer service counter");
        channels.add("email");
        predefinedChannels = channels;
        minAge = 0;
        maxAge = 99;
        nationalities = customerSession.getAllNationalities();

    }

    public CustomerManager() {
    }

    public String toViewFeedbacks() {
        if (selectedCustomer != null) {
            feedbacks = getRegCustFeedbacks();
            return crmNavController.redirectToViewFeedbacks();
        }
        msgController.addErrorMessage("Please select an customer");
        return "";
    }
    
    public String toViewCustomers(){
        return crmNavController.redirectToViewCustomers();
    }
        

    public String toViewCustomerParticulars() {
        if (selectedCustomer != null) {
            if (selectedCustomer.getDType().equals("RegCust")) {
                selectedRegCust = customerSession.findRegCustByCustomer(selectedCustomer);
                System.out.println(selectedRegCust.getEmail());
                return crmNavController.redirectToViewRegCustomerParticulars();
            } else {
                return crmNavController.redirectToViewCustomerParticulars();
            }
        }
        msgController.addErrorMessage("Please select an customer");
        return "";
    }

    public String toCreateFeedback() {
        System.out.println("Redirecting to create feedback page");
        System.out.println("Number of feedback channels: " + predefinedChannels.size());
        return crmNavController.redirectToCreateFeedback();

//        if (selectedCustomer != null) {
//            if (selectedCustomer.getDType().equals("RegCust")) {
//                selectedRegCust = customerSession.findRegCustByCustomer(selectedCustomer);
//                return crmNavController.redirectToCreateFeedback();
//            } 
//        }
//        msgController.addErrorMessage("Please select an customer");
//        return "";
    }

    public String toSegmentCustomers() {
        return crmNavController.redirectToSegmentCustomers();
    }

    public void searchCustomer() {
        System.out.println("start to search customer");
        customers = customerSession.searchCustomer(passportNo, email, firstName, lastName, membershipId);
        System.out.println(customers.size() + " results found");

    }

    public void searchCustomerByPassportNo() {
        System.out.println("Start to search customer by passport number");
        System.out.println("The input passport number is:" + passportNo);

        customers = customerSession.getCustomerByPassportNo(passportNo);
        System.out.println("Customer " + customers.get(0).getId() + " is found");

    }

    public void searchCustomerByName() {
        System.out.println("start to search customer");
        customers = customerSession.getCustomerByName(firstName, lastName);
        System.out.println(customers.size() + " results found");

    }

    public void searchCustomerByMembershipId() {
        customers = customerSession.getCustomerByMembershipId(membershipId);
    }

    public String createFeedback() {
        customerSession.createFeedback(selectedRegCust, feedbackSubject, feedbackContent, feedbackChannel);
        feedbackChannel = null;
        feedbackSubject = null;
        feedbackContent = null;
        toViewFeedbacks();
        return crmNavController.redirectToViewFeedbacks();
    }

    public void resetSearchCustomer() {
        customers = getAllCustomers();
        passportNo = null;
        firstName = null;
        lastName = null;
        membershipId = null;

    }
    
    public void resetCustomer() {
        customers = getAllCustomers();
    }

    public void resetSegmentCustomers() {
        regCusts = customerSession.getAllRegCusts();
    }

//    public void searchCustomerByFirstName(String firstName) {
//        customers = customerSession.searchCustomerByFirstName(firstName);
//    }
//
//    public void searchCustomerByLastName(String familyName) {
//        customers = customerSession.searchCustomerByFirstName(familyName);
//    }
//
//    public void searchCustomerByPassportNumber(String passportNumber) {
//        customers = customerSession.searchCustomerByFirstName(passportNumber);
//    }
    public List<Feedback> getRegCustFeedbacks() {
        return customerSession.getRegCustFeedback(selectedRegCust);

    }

    public String createCustomerList() {
        String customerListName = new String("Customer list: ");
        if (gender != null) {
            customerListName = customerListName.concat(gender);
        }
        customerListName = customerListName.concat("-");
        if (nationality != null) {
            customerListName = customerListName.concat(nationality);
        }
        customerListName = customerListName.concat("- Age:");
        customerListName = customerListName.concat(Integer.toString(minAge));
        customerListName = customerListName.concat("-");
        customerListName = customerListName.concat(Integer.toString(maxAge));
        System.out.println("Managed bean: create name: " + customerListName);

        customerList = customerSession.createCustomerList(regCusts, customerListName);
        System.out.println("customer list created");
        customerLists = customerSession.getAllCustomerLists();

        return crmNavController.redirectToViewCustomerLists();

    }

    public void segmentCustomers() {
        regCusts = customerSession.filterCustomer(gender, nationality, minAge, maxAge);
        System.out.println("Current size of customers is: " + regCusts.size());
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

    public List<String> getPredefinedChannels() {
        return predefinedChannels;
    }

    public void setPredefinedChannels(List<String> predefinedChannels) {
        this.predefinedChannels = predefinedChannels;
    }

    public String getFeedbackSubject() {
        return feedbackSubject;
    }

    public void setFeedbackSubject(String feedbackSubject) {
        this.feedbackSubject = feedbackSubject;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getFeedbackChannel() {
        return feedbackChannel;
    }

    public void setFeedbackChannel(String feedbackChannel) {
        this.feedbackChannel = feedbackChannel;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public List<String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(List<String> nationalities) {
        this.nationalities = nationalities;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public List<RegCust> getRegCusts() {
        return regCusts;
    }

    public void setRegCusts(List<RegCust> regCusts) {
        this.regCusts = regCusts;
    }

    public List<RegCust> getFilteredRegCusts() {
        return filteredRegCusts;
    }

    public void setFilteredRegCusts(List<RegCust> filteredRegCusts) {
        this.filteredRegCusts = filteredRegCusts;
    }

    public CustomerList getCustomerList() {
        return customerList;
    }

    public void setCustomerList(CustomerList customerList) {
        this.customerList = customerList;
    }

    public List<CustomerList> getCustomerLists() {
        return customerLists;
    }

    public void setCustomerLists(List<CustomerList> customerLists) {
        this.customerLists = customerLists;
    }

    public String getCustomerListId() {
        return customerListId;
    }

    public void setCustomerListId(String customerListId) {
        this.customerListId = customerListId;
    }

}
