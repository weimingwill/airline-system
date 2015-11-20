/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.crm;

import ams.crm.entity.MktCampaign;
import ams.crm.session.AnalyticsSessionLocal;
import ams.crm.session.CampaignSessionLocal;
import ams.crm.session.CustomerSessionLocal;
import ams.crm.session.PurchaseBehaviorSessionLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
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

    @EJB
    private AnalyticsSessionLocal analyticsSession;

    @EJB
    private PurchaseBehaviorSessionLocal purchaseBehaviorSession;

    @EJB
    private CampaignSessionLocal campaignSession;

    private BarChartModel barChartModel;
    private BarChartModel ageDistributionBarChart;
    private PieChartModel pieChartModel;
    private DashboardModel customerDashboard;
    private BarChartModel animatedModel1;
    private MeterGaugeChartModel customerNo;
    private MeterGaugeChartModel memberNo;
    private DonutChartModel travellerType;
    private DonutChartModel channelDistribution;
    private DonutChartModel customerType;
    private List<MktCampaign> campaigns;
    private int campaignNo;
    private int ongoingCamapaignNo;
    private DashboardModel behaviorDashboard;
    private double addOnRate;
    private DefaultDashboardModel salesDashboard;
    private double daySales;
    private double monthSales;
    private double yearSales;
//    private double[] monthlySales;
//    private double[] yearlySales;
    private LineChartModel monthlySalesLineChart;
    private LineChartModel yearlySalesLineChart;

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

        createCustomerDashboard();

        createBehaviorDashboard();

        getAgeDistribution();

        createMeterGaugeModels();

        campaigns = campaignSession.getAllCampaigns();

        campaignNo = campaignSession.getCampaignNo();

        ongoingCamapaignNo = campaignSession.getOngoingCampaignNo();

        addOnRate = purchaseBehaviorSession.getAddonPurchaseRate();

        createTravellerTypeDonutChart();

        createCustomerTypeDonutChart();

        addOnRate = purchaseBehaviorSession.getAddonPurchaseRate();

        createChannelDistributionDonutChart();

        daySales = analyticsSession.getTodaySales();

        monthSales = analyticsSession.getCurrentMonthSales();

        yearSales = analyticsSession.getCurrentYearSales();

        createMonthlySalesLinearModel();

        createYearlySalesLinearModel();
        
        createSalesDashboard();

//        monthlySales = analyticsSession.getMonthlySales();
//
//        yearlySales = analyticsSession.getYearlySales();
    }

    public AnalyticsController() {
    }

    public void createCustomerDashboard() {

        customerDashboard = new DefaultDashboardModel();

        DashboardColumn column1 = new DefaultDashboardColumn();

        DashboardColumn column2 = new DefaultDashboardColumn();

        column1.addWidget("customerStats");

        column1.addWidget("ageDistribution");

//        column1.addWidget("genderDistribution");
        column2.addWidget("locationDistribution");

        column2.addWidget("misc");

        customerDashboard.addColumn(column1);

        customerDashboard.addColumn(column2);

    }

    public void createBehaviorDashboard() {

        behaviorDashboard = new DefaultDashboardModel();

        DashboardColumn column3 = new DefaultDashboardColumn();

        DashboardColumn column4 = new DefaultDashboardColumn();

        column3.addWidget("travellerType");

        column3.addWidget("customerType");

        column4.addWidget("addOnRate");

        column4.addWidget("channelDistribution");

        behaviorDashboard.addColumn(column3);

        behaviorDashboard.addColumn(column4);

    }

    public void createSalesDashboard() {

        salesDashboard = new DefaultDashboardModel();

        DashboardColumn column1 = new DefaultDashboardColumn();

        DashboardColumn column2 = new DefaultDashboardColumn();

        DashboardColumn column3 = new DefaultDashboardColumn();

        column1.addWidget("daySales");

        column2.addWidget("monthSales");

        column3.addWidget("yearSales");

        column1.addWidget("monthlySales");

        column2.addWidget("yearlySales");

        customerDashboard.addColumn(column1);

        customerDashboard.addColumn(column2);

        customerDashboard.addColumn(column3);

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
        BarChartModel model = new BarChartModel();

        ChartSeries male = new ChartSeries();

        male.setLabel("Male");

        male.set("Male", customerSession.getTotalNumberOfMale());

        ChartSeries female = new ChartSeries();

        female.setLabel("Female");

        female.set("Female", customerSession.getTotalNumberOfFemale());

        model.addSeries(male);

        model.addSeries(female);

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

    private void getAgeDistribution() {
        ageDistributionBarChart = new BarChartModel();

        int[] ageGroup = new int[9];
        ageGroup = analyticsSession.getAllCustomerAges();

        ChartSeries ageGroups = new ChartSeries();
        ageGroups.setLabel("Age Group");
        ageGroups.set("< 2", ageGroup[0]);
        ageGroups.set("2 - 11", ageGroup[1]);
        ageGroups.set("12 - 18", ageGroup[2]);
        ageGroups.set("19 - 24", ageGroup[3]);
        ageGroups.set("25 - 30", ageGroup[4]);
        ageGroups.set("31 - 40", ageGroup[5]);
        ageGroups.set("41 - 55", ageGroup[6]);
        ageGroups.set("55 - 65", ageGroup[7]);
        ageGroups.set("> 65", ageGroup[8]);

        ageDistributionBarChart.addSeries(ageGroups);
        createAnimatedBarChart(ageDistributionBarChart);

    }

    private void createAnimatedBarChart(BarChartModel bcm) {
        bcm.setTitle("Age Distribution");
        bcm.setAnimate(true);
        bcm.setLegendPosition("se");
        bcm.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
        Axis yAxis = bcm.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(15);
        animatedModel1 = bcm;
    }

    private MeterGaugeChartModel getTotalCustomerNo() {
        List<Number> intervals = new ArrayList<Number>() {
            {
                add(20);
                add(40);
                add(60);
                add(80);
            }
        };
        return new MeterGaugeChartModel(customerSession.getTotalCustomerNo(), intervals);
    }

    private MeterGaugeChartModel getTotalMemberNo() {
        List<Number> intervals = new ArrayList<Number>() {
            {
                add(20);
                add(40);
                add(60);
                add(80);
            }
        };
        return new MeterGaugeChartModel(customerSession.getTotalMemberNo(), intervals);
    }

    private void createMeterGaugeModels() {
        customerNo = getTotalCustomerNo();
        customerNo.setTitle("Total number of customers");
        customerNo.setGaugeLabel("");

        memberNo = getTotalMemberNo();
        memberNo.setTitle("Total number of frequent flyers");
        memberNo.setSeriesColors("66cc66,93b75f,E7E658,cc6666");
        memberNo.setGaugeLabel("");
        memberNo.setGaugeLabelPosition("bottom");
        memberNo.setShowTickLabels(true);
        memberNo.setLabelHeightAdjust(110);
        memberNo.setIntervalOuterRadius(100);
    }

    private void createTravellerTypeDonutChart() {
        travellerType = new DonutChartModel();
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        circle1.put("Business traveller", purchaseBehaviorSession.getBizTravellerNo());
        circle1.put("Lersure traveller", purchaseBehaviorSession.getLeisureTravellerNo());
        travellerType.addCircle(circle1);

        travellerType.setTitle("Business vs Leisure travellers");
        travellerType.setLegendPosition("w");
        travellerType.setSliceMargin(5);
        travellerType.setShowDataLabels(true);
        travellerType.setDataFormat("value");
        travellerType.setShadow(false);
    }

    private void createCustomerTypeDonutChart() {
        travellerType = new DonutChartModel();
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        circle1.put("New customer", purchaseBehaviorSession.getNewCustomerNo());
        circle1.put("Return customer", purchaseBehaviorSession.getReturningCustomerNo());
        travellerType.addCircle(circle1);

        travellerType.setTitle("New vs Return customers");
        travellerType.setLegendPosition("w");
        travellerType.setSliceMargin(5);
        travellerType.setShowDataLabels(true);
        travellerType.setDataFormat("value");
        travellerType.setShadow(false);
    }

    private void createChannelDistributionDonutChart() {
        travellerType = new DonutChartModel();
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        circle1.put("ARS", purchaseBehaviorSession.getArsBookingNo());
        circle1.put("DDS", purchaseBehaviorSession.getDdsBookingNo());
        travellerType.addCircle(circle1);

        travellerType.setTitle("ARS vs DDS");
        travellerType.setLegendPosition("w");
        travellerType.setSliceMargin(5);
        travellerType.setShowDataLabels(true);
        travellerType.setDataFormat("value");
        travellerType.setShadow(false);
    }

    private void createMonthlySalesLinearModel() {

        double[] monthlySales = new double[12];
        monthlySales = analyticsSession.getMonthlySales();

        monthlySalesLineChart = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Sales");

        series1.set("Jan", monthlySales[0]);
        series1.set("Feb", monthlySales[1]);
        series1.set("Mar", monthlySales[2]);
        series1.set("Apr", monthlySales[3]);
        series1.set("May", monthlySales[4]);
        series1.set("Jun", monthlySales[5]);
        series1.set("Jul", monthlySales[6]);
        series1.set("Aug", monthlySales[7]);
        series1.set("Sep", monthlySales[8]);
        series1.set("Oct", monthlySales[9]);
        series1.set("Nov", monthlySales[10]);
        series1.set("Dec", monthlySales[11]);

        monthlySalesLineChart.addSeries(series1);

        monthlySalesLineChart.setTitle("Monthly sales distribution");
        monthlySalesLineChart.setLegendPosition("e");
        Axis yAxis = monthlySalesLineChart.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(10000);
    }

    private void createYearlySalesLinearModel() {

        double[] yearlySales = new double[5];
        yearlySales = analyticsSession.getYearlySales();

        yearlySalesLineChart = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Sales");

        int year = Calendar.getInstance().get(Calendar.YEAR);

        series1.set(year - 4, yearlySales[0]);
        series1.set(year - 3, yearlySales[1]);
        series1.set(year - 2, yearlySales[2]);
        series1.set(year - 1, yearlySales[3]);
        series1.set(year, yearlySales[4]);

        monthlySalesLineChart.addSeries(series1);

        monthlySalesLineChart.setTitle("Yearly sales distribution");
        monthlySalesLineChart.setLegendPosition("e");
        Axis yAxis = monthlySalesLineChart.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(10000);
    }

    public String getPromoCodeByCampaign(MktCampaign mc) {
        
        return campaignSession.getPromoCodeByCampaign(mc);

    }

//***********************************************getter & setter***********************************************
//*************************************************************************************************************
//*************************************************************************************************************
//*************************************************************************************************************
//*************************************************************************************************************  
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

    public AnalyticsSessionLocal getAnalyticsSession() {
        return analyticsSession;
    }

    public void setAnalyticsSession(AnalyticsSessionLocal analyticsSession) {
        this.analyticsSession = analyticsSession;
    }

    public BarChartModel getAgeDistributionBarChart() {
        return ageDistributionBarChart;
    }

    public void setAgeDistributionBarChart(BarChartModel ageDistributionBarChart) {
        this.ageDistributionBarChart = ageDistributionBarChart;
    }

    public BarChartModel getAnimatedModel1() {
        return animatedModel1;
    }

    public void setAnimatedModel1(BarChartModel animatedModel1) {
        this.animatedModel1 = animatedModel1;
    }

    public MeterGaugeChartModel getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(MeterGaugeChartModel customerNo) {
        this.customerNo = customerNo;
    }

    public MeterGaugeChartModel getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(MeterGaugeChartModel memberNo) {
        this.memberNo = memberNo;
    }

    public PurchaseBehaviorSessionLocal getPurchaseBehaviorSession() {
        return purchaseBehaviorSession;
    }

    public void setPurchaseBehaviorSession(PurchaseBehaviorSessionLocal purchaseBehaviorSession) {
        this.purchaseBehaviorSession = purchaseBehaviorSession;
    }

    public DonutChartModel getTravellerType() {
        return travellerType;
    }

    public void setTravellerType(DonutChartModel travellerType) {
        this.travellerType = travellerType;
    }

    public DonutChartModel getChannelDistribution() {
        return channelDistribution;
    }

    public void setChannelDistribution(DonutChartModel channelDistribution) {
        this.channelDistribution = channelDistribution;
    }

    public DonutChartModel getCustomerType() {
        return customerType;
    }

    public void setCustomerType(DonutChartModel customerType) {
        this.customerType = customerType;
    }

    public CampaignSessionLocal getCampaignSession() {
        return campaignSession;
    }

    public void setCampaignSession(CampaignSessionLocal campaignSession) {
        this.campaignSession = campaignSession;
    }

    public List<MktCampaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<MktCampaign> campaigns) {
        this.campaigns = campaigns;
    }

    public int getCampaignNo() {
        return campaignNo;
    }

    public void setCampaignNo(int campaignNo) {
        this.campaignNo = campaignNo;
    }

    public int getOngoingCamapaignNo() {
        return ongoingCamapaignNo;
    }

    public void setOngoingCamapaignNo(int ongoingCamapaignNo) {
        this.ongoingCamapaignNo = ongoingCamapaignNo;
    }

    public DashboardModel getBehaviorDashboard() {
        return behaviorDashboard;
    }

    public void setBehaviorDashboard(DashboardModel behaviorDashboard) {
        this.behaviorDashboard = behaviorDashboard;
    }

    public double getAddOnRate() {
        return addOnRate;
    }

    public void setAddOnRate(double addOnRate) {
        this.addOnRate = addOnRate;
    }

    public DefaultDashboardModel getSalesDashboard() {
        return salesDashboard;
    }

    public void setSalesDashboard(DefaultDashboardModel salesDashboard) {
        this.salesDashboard = salesDashboard;
    }

    public double getDaySales() {
        return daySales;
    }

    public void setDaySales(double daySales) {
        this.daySales = daySales;
    }

    public double getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(double monthSales) {
        this.monthSales = monthSales;
    }

    public double getYearSales() {
        return yearSales;
    }

    public void setYearSales(double yearSales) {
        this.yearSales = yearSales;
    }

//    public double[] getMonthlySales() {
//        return monthlySales;
//    }
//
//    public void setMonthlySales(double[] monthlySales) {
//        this.monthlySales = monthlySales;
//    }
//
//    public double[] getYearlySales() {
//        return yearlySales;
//    }
//
//    public void setYearlySales(double[] yearlySales) {
//        this.yearlySales = yearlySales;
//    }
    public LineChartModel getMonthlySalesLineChart() {
        return monthlySalesLineChart;
    }

    public void setMonthlySalesLineChart(LineChartModel monthlySalesLineChart) {
        this.monthlySalesLineChart = monthlySalesLineChart;
    }

    public LineChartModel getYearlySalesLineChart() {
        return yearlySalesLineChart;
    }

    public void setYearlySalesLineChart(LineChartModel yearlySalesLineChart) {
        this.yearlySalesLineChart = yearlySalesLineChart;
    }

}
