/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams.crm.session;

import ams.aps.entity.FlightSchedule;
import ams.crm.entity.MktCampaign;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Tongtong
 */
@Local
public interface AnalyticsSessionLocal {
    
    public int getTodayBookingNo();
    
    public double getCurrentMonthSales();
    
    public double getCurrentYearSales();
    
    public double getTodaySales();
    
    
    //get monthly sales
    public double[] getMonthlySales();
    
    //get recent yearly sales for recent 5 years 
    public double[] getYearlySales();
    
    public int getTotalCampaignNo();
    
    public int getOngoingCampaignNo();
    
    public List<MktCampaign> getAllOngoingCampaigns();
    
    public List<MktCampaign> getAllCampaigns();
 //    public FlightSchedule getMostPopularFlightSchedule();
    
    
    
}
