/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface PurchaseBehaviorSessionLocal {
    
    public int getBizTravellerNo();
    
    public int getLeisureTravellerNo();
    
    public int getNewCustomerNo();
    
    public int getReturningCustomerNo();
    
    public double getAddonPurchaseRate();
    
    public int getArsBookingNo();
    
    public int getDdsBookingNo();
    
    
    
}
