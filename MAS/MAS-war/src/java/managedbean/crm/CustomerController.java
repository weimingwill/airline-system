/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.RegCust;
import ams.crm.entity.helper.Phone;
import ams.crm.session.CustomerSessionLocal;
import ams.crm.util.exception.NoSuchRegCustException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import managedbean.application.MsgController;
import managedbean.application.NavigationController;

/**
 *
 * @author Bowen
 */
@Named(value = "customerController")
@RequestScoped
public class CustomerController implements Serializable {
    @Inject
    private NavigationController navigationController;
    @Inject
    private MsgController msgController;
 
    @EJB
    private CustomerSessionLocal customerSession;
   
    private String email;
    private String password;

    
    private String title;
    private String firstname;
    private String lastname;
    private String passportNo;
    private String gender;
    private String nationality;
    private Date dob;
    private String addr1;
    private String addr2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Phone phone;
    private String securQest;
    private String securAns;
    private Boolean newsLetterPref;
    private Boolean promoPref;
    private String membershipClass;
    private Double accMiles;
    private Double custValue;
    private Boolean activated;
    private Integer numofFlights;
    private String membershipId;
 
    
    public CustomerController() {
    }
    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.email = (String) sessionMap.get("email");
        initializeCustomer();
    }
    
    public RegCust getRegCustByEmail() throws NoSuchRegCustException{
       return  customerSession.getRegCustByEmail(email);  
    }
    
    public void initializeCustomer() {
        try {
            RegCust regCust = getRegCustByEmail();
            email = regCust.getEmail();
            firstname=regCust.getFirstName();
            lastname=regCust.getLastName();
            password = regCust.getPwd();
            title = regCust.getTitle();
            passportNo = regCust.getPassportNo();
            gender=regCust.getGender();
            nationality=regCust.getNationality();
            dob=regCust.getDob();
            addr1=regCust.getAddr1();
            addr2=regCust.getAddr2();
            city=regCust.getCity();
            state=regCust.getProvince();
            country=regCust.getCountry();
            zipCode=regCust.getZipCode();
            phone=regCust.getPhone();
            securQest=regCust.getSecurQuest();
            securAns=regCust.getSecurAns();
            newsLetterPref=regCust.getNewsLetterPref();
            promoPref=regCust.getPromoPref();
            membershipClass=regCust.getMembershipClass();
            accMiles=regCust.getAccMiles();
            custValue=regCust.getCustValue();
            activated=regCust.getActivated();
            numofFlights=regCust.getNumOfFlights();
            membershipId=regCust.getMemberShipId();
            
        } catch (NoSuchRegCustException ex) {
            email = null;
            firstname=null;
            lastname=null;
            password = null;
            title = null;
            passportNo = null;
            gender=null;
            nationality=null;
            dob=null;
            addr1=null;
            addr2=null;
            city=null;
            state=null;
            country=null;
            zipCode=null;
            phone=null;
            securQest=null;
            securAns=null;
            newsLetterPref=null;
            promoPref=null;
            membershipClass=null;
            accMiles=null;
            custValue=null;
            activated=null;
            numofFlights=null;
            membershipId=null;
        }
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getSecurQest() {
        return securQest;
    }

    public void setSecurQest(String securQest) {
        this.securQest = securQest;
    }

    public String getSecurAns() {
        return securAns;
    }

    public void setSecurAns(String securAns) {
        this.securAns = securAns;
    }

    public Boolean getNewsLetterPref() {
        return newsLetterPref;
    }

    public void setNewsLetterPref(Boolean newsLetterPref) {
        this.newsLetterPref = newsLetterPref;
    }

    public Boolean getPromoPref() {
        return promoPref;
    }

    public void setPromoPref(Boolean promoPref) {
        this.promoPref = promoPref;
    }

    public String getMembershipClass() {
        return membershipClass;
    }

    public void setMembershipClass(String membershipClass) {
        this.membershipClass = membershipClass;
    }

    public Double getAccMiles() {
        return accMiles;
    }

    public void setAccMiles(Double accMiles) {
        this.accMiles = accMiles;
    }

    public Double getCustValue() {
        return custValue;
    }

    public void setCustValue(Double custValue) {
        this.custValue = custValue;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Integer getNumofFlights() {
        return numofFlights;
    }

    public void setNumofFlights(Integer numofFlights) {
        this.numofFlights = numofFlights;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }
}
