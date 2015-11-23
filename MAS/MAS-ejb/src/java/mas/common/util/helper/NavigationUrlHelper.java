/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.helper;

/**
 *
 * @author Lewis
 */
public class NavigationUrlHelper {

    private final static String REDIRECT = "?faces-redirect=true";
    private final static String APS_PATH = "/views/internal/secured/aps/";
    private final static String AIS_PATH = "/views/internal/secured/ais/";
    private final static String AFOS_PATH = "/views/internal/secured/afos/";
    private final static String ARS_PATH = "/views/internal/secured/ars/";
    private final static String DCS_PATH = "/views/internal/secured/dcs/";
    private final static String AAS_PATH = "/views/internal/secured/aas/";
    private final static String CRM_PATH = "/views/internal/secured/crm/";

    // System & Module Url
    public final static String APS_URL = APS_PATH + "apsMain.xhtml" + REDIRECT;
    public final static String APS_FP_URL = APS_PATH + "fleet/viewFleet.xhtml" + REDIRECT;
    public final static String APS_RP_URL = APS_PATH + "route/viewRoutes.xhtml" + REDIRECT;
    public final static String APS_FS_URL = APS_PATH + "flight_schedule/createFlight.xhtml" + REDIRECT;

    public final static String AIS_URL = AIS_PATH + "aisMain.xhtml" + REDIRECT;
    public final static String AIS_PD_URL = AIS_PATH + "product_design/productDesign.xhtml" + REDIRECT;
    public final static String AIS_IM_URL = AIS_PATH + "booking_class/viewFlightSchedule.xhtml" + REDIRECT;

    public final static String AFOS_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_BM_URL = AFOS_PATH + "crew_schedule/selectBiddingSession.xhtml" + REDIRECT;
    public final static String AFOS_MM_URL = AFOS_PATH + "maint_mgmt/viewMaintSchedule.xhtml" + REDIRECT;
    public final static String AFOS_CD_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_FCM_URL = AFOS_PATH + "crew_schedule/viewFlightCrewProfile.xhtml" + REDIRECT;
    
    public final static String ARS_URL = ARS_PATH + "index.xhtml" + REDIRECT;
    public final static String ARS_BM_URL = ARS_PATH + "manage_booking/myBooking.xhtml" + REDIRECT;
    public final static String ARS_DDS_URL = ARS_PATH + "index.xhtml" + REDIRECT;
    
    public final static String DCS_URL = DCS_PATH + "checkInPassenger.xhtml" + REDIRECT;
    public final static String DCS_CM_URL = DCS_PATH + "checkInPassenger.xhtml" + REDIRECT;
    public final static String DCS_BM_URL = DCS_PATH + "checkInLuggage.xhtml" + REDIRECT;
    
    public final static String AAS_URL = AAS_PATH + "viewRevenueReport.xhtml" + REDIRECT;
    public final static String AAS_RA_URL = AAS_PATH + "generateRevenueReport.xhtml" + REDIRECT;
    public final static String AAS_CA_URL = AAS_PATH + "generateRevenueReport.xhtml" + REDIRECT;
    public final static String AAS_FA_URL = AAS_PATH + "generateRevenueReport.xhtml" + REDIRECT;
    public final static String AAS_PR_URL = AAS_PATH + "payroll/crewPayroll.xhtml" + REDIRECT;
    
    public final static String CRM_URL = CRM_PATH + "crmMain.xhtml" + REDIRECT;
    public final static String CRM_CM_URL = CRM_PATH + "customer_management/viewCustomers.xhtml" + REDIRECT;
    public final static String CRM_CPM_URL = CRM_PATH + "campaign_management/viewCampaigns.xhtml" + REDIRECT;
    public final static String CRM_CA_URL = CRM_PATH + "analytics/customerAnalysis.xhtml" + REDIRECT;
}
