/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.ars_crm.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Tongtong
 */
@Entity
public class Member extends Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String phoneNo;
    private String address1;
    private String address2;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private String password;
    private String secureQ;
    private String secureQAns;
    private boolean newsletterPreference;
    private boolean promotionPreference;
    private String membershipClass;
    private double accMiles;
    private double custValue;
    private int noOfFlights;
        

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecureQ() {
        return secureQ;
    }

    public void setSecureQ(String secureQ) {
        this.secureQ = secureQ;
    }

    public String getSecureQAns() {
        return secureQAns;
    }

    public void setSecureQAns(String secureQAns) {
        this.secureQAns = secureQAns;
    }

    public boolean isNewsletterPreference() {
        return newsletterPreference;
    }

    public void setNewsletterPreference(boolean newsletterPreference) {
        this.newsletterPreference = newsletterPreference;
    }

    public boolean isPromotionPreference() {
        return promotionPreference;
    }

    public void setPromotionPreference(boolean promotionPreference) {
        this.promotionPreference = promotionPreference;
    }

    public String getMembershipClass() {
        return membershipClass;
    }

    public void setMembershipClass(String membershipClass) {
        this.membershipClass = membershipClass;
    }

    public double getAccMiles() {
        return accMiles;
    }

    public void setAccMiles(double accMiles) {
        this.accMiles = accMiles;
    }

    public double getCustValue() {
        return custValue;
    }

    public void setCustValue(double custValue) {
        this.custValue = custValue;
    }

    public int getNoOfFlights() {
        return noOfFlights;
    }

    public void setNoOfFlights(int noOfFlights) {
        this.noOfFlights = noOfFlights;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Member)) {
            return false;
        }
        Member other = (Member) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ams.ars_crm.entity.Member[ id=" + id + " ]";
    }

}
