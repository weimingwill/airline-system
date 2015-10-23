/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.entity;

import ams.crm.entity.helper.Phone;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Tongtong
 */
@Entity
public class RegCust extends Customer implements Serializable {

    private String email;
    private String pwd;
    @Embedded
    private Phone telephone;
    @Embedded
    private Phone mobilephone;
    private String addr1;
    private String addr2;
    private String zipCode;
    private String city;
    private String province;
    private String country;
    private String securQuest;
    private String securAns;
    private Boolean newsLetterPref;
    private Boolean promoPref;
    private String membershipClass;
    private Double accMiles;
    private Double custValue;
    private Integer numOfFlights;
    private String memberShipId;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<MileTrans> mileTranses = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<Feedback> feedbacks = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Membership membership;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Phone getTelephone() {
        return telephone;
    }

    public void setTelephone(Phone telephone) {
        this.telephone = telephone;
    }

    public Phone getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(Phone mobilephone) {
        this.mobilephone = mobilephone;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSecurQuest() {
        return securQuest;
    }

    public void setSecurQuest(String securQuest) {
        this.securQuest = securQuest;
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

    public Integer getNumOfFlights() {
        return numOfFlights;
    }

    public void setNumOfFlights(Integer numOfFlights) {
        this.numOfFlights = numOfFlights;
    }

    public String getMemberShipId() {
        return memberShipId;
    }

    public void setMemberShipId(String memberShipId) {
        this.memberShipId = memberShipId;
    }

    public List<MileTrans> getMileTranses() {
        return mileTranses;
    }

    public void setMileTranses(List<MileTrans> mileTranses) {
        this.mileTranses = mileTranses;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

}
