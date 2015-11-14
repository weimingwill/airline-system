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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

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
    private DashboardModel customerDashboard;

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
        customerDashboard = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();

        DashboardColumn column2 = new DefaultDashboardColumn();

        DashboardColumn column3 = new DefaultDashboardColumn();

        column1.addWidget("Customer Stats");

        column1.addWidget("Gender Distribution");

        column1.addWidget("Age Distribution");

        column2.addWidget("(Member) Location Distribution");

        column2.addWidget("(Member) Loyalty Stats");

        column3.addWidget("Miscellaneous");

        customerDashboard.addColumn(column1);
        System.out.println("Dashboard step 11");

        customerDashboard.addColumn(column2);
        System.out.println("Dashboard step 12");

        customerDashboard.addColumn(column3);
        System.out.println("Dashboard step 3");

    }

    public AnalyticsController() {
    }

    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

        addMessage(message);
    }

    public void handleClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");

        addMessage(message);
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
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

//***********************************************getter & setter**************************************************
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

    public DashboardModel getCustomerDashboard() {
        return customerDashboard;
    }

    public void setCustomerDashboard(DashboardModel customerDashboard) {
        this.customerDashboard = customerDashboard;
    }

}
