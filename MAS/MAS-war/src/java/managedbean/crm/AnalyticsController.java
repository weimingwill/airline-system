/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.session.CustomerSessionLocal;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author Tongtong
 */
@Named(value = "analyticsController")
@RequestScoped
public class AnalyticsController {
    
    @EJB 
    private CustomerSessionLocal customerSession;
    
    private BarChartModel barChartModel;

    /**
     * Creates a new instance of AnalyticsController
     * 
     * 
     */
    
    public void init(){
        barChartModel = initBarModel();
    }
    
    
    public AnalyticsController() {
    }
    
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries male = new ChartSeries();
        male.setLabel("Male");
        male.set("Male", customerSession.getTotalNumberOfMale());

 
        ChartSeries female = new ChartSeries();
        female.setLabel("Female");
        female.set("Female", customerSession.getTotalNumberOfFemale());

 
        model.addSeries(male);
        model.addSeries(female);
         
        return model;
    }

    public CustomerSessionLocal getCustomerSession() {
        return customerSession;
    }

    public void setCustomerSession(CustomerSessionLocal customerSession) {
        this.customerSession = customerSession;
    }

    public BarChartModel getBarChartModel() {
        return barChartModel;
    }

    public void setBarChartModel(BarChartModel barChartModel) {
        this.barChartModel = barChartModel;
    }
    
    
    
}
