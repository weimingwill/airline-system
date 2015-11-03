/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.util.helper;

import ams.aps.entity.Airport;
import java.util.Date;
import java.util.List;

/**
 *
 * @author weiming
 */
public class SearchFlightHelper {

    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private Airport deptAirport;
    private Airport arrAirport;
    private List<Airport> allAirports;
    private Date deptDate;
    private Date arrDate;
    private boolean showPremium;
    private String promoCode;
    private String choice;

    public SearchFlightHelper() {
    }
    
    public int getAdultNo() {
        return adultNo;
    }

    public void setAdultNo(int adultNo) {
        this.adultNo = adultNo;
    }

    public int getChildrenNo() {
        return childrenNo;
    }

    public void setChildrenNo(int childrenNo) {
        this.childrenNo = childrenNo;
    }

    public int getInfantNo() {
        return infantNo;
    }

    public void setInfantNo(int infantNo) {
        this.infantNo = infantNo;
    }

    public Airport getDeptAirport() {
        return deptAirport;
    }

    public void setDeptAirport(Airport deptAirport) {
        this.deptAirport = deptAirport;
    }

    public Airport getArrAirport() {
        return arrAirport;
    }

    public void setArrAirport(Airport arrAirport) {
        this.arrAirport = arrAirport;
    }

    public List<Airport> getAllAirports() {
        return allAirports;
    }

    public void setAllAirports(List<Airport> allAirports) {
        this.allAirports = allAirports;
    }

    public Date getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(Date deptDate) {
        this.deptDate = deptDate;
    }

    public Date getArrDate() {
        return arrDate;
    }

    public void setArrDate(Date arrDate) {
        this.arrDate = arrDate;
    }

    public boolean isShowPremium() {
        return showPremium;
    }

    public void setShowPremium(boolean showPremium) {
        this.showPremium = showPremium;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
    
    
    
}
