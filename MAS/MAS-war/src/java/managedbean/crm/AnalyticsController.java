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
import javax.annotation.PostConstruct;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

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
    private PieChartModel pieChartModel;

    /**
     * Creates a new instance of AnalyticsController
     *
     *
     */
    @PostConstruct
    public void init() {
        System.out.println("analytics controller is started");
        barChartModel = initBarModel();
        createPieModel();
    }

    public AnalyticsController() {
    }

    private BarChartModel initBarModel() {
        System.out.println("start to initiate bar model");
        BarChartModel model = new BarChartModel();
        System.out.println("Initiating bar model: 1");

        ChartSeries male = new ChartSeries();
        System.out.println("Initiating bar model: 2");

        male.setLabel("Male");
        System.out.println("Initiating bar model: 3");

        male.set("Male", customerSession.getTotalNumberOfMale());
        System.out.println("Initiating bar model: 4");

        ChartSeries female = new ChartSeries();
        System.out.println("Initiating bar model: 5");

        female.setLabel("Female");
        System.out.println("Initiating bar model: 6");

        female.set("Female", customerSession.getTotalNumberOfFemale());
        System.out.println("Initiating bar model: 7");

        model.addSeries(male);
        System.out.println("Initiating bar model: 8");

        model.addSeries(female);
        System.out.println("Initiating bar model: 9");
        
        model.setTitle("Gender Distribution");

        return model;
    }

    private void createPieModel() {
        pieChartModel = new PieChartModel();

        pieChartModel.set("Female", customerSession.getTotalNumberOfFemale());
        pieChartModel.set("Male", customerSession.getTotalNumberOfMale());
        pieChartModel.setTitle("Gender distribution piechart");


        pieChartModel.setLegendPosition("w");
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

    public PieChartModel getPieChartModel() {
        return pieChartModel;
    }

    public void setPieChartModel(PieChartModel pieChartModel) {
        this.pieChartModel = pieChartModel;
    }
    
    

}
