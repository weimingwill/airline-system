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
    private final static String COMMON_PATH = "/views/internal/secured/common/";
    private final static String APS_PATH = "/views/internal/secured/aps/";
    private final static String AIS_PATH = "/views/internal/secured/ais/";
    private final static String AFOS_PATH = "/views/internal/secured/afos/";
    
    // System & Module Url
    public final static String APS_URL = APS_PATH + "apsMain.xhtml" + REDIRECT;
    public final static String APS_FP_URL = APS_PATH + "fleet/viewFleet.xhtml" + REDIRECT;
    public final static String APS_RP_URL = APS_PATH + "route/viewRoutes.xhtml" + REDIRECT;
    public final static String APS_FS_URL = APS_PATH + "flight_schedule/createFlight.xhtml" + REDIRECT;
    
    public final static String AIS_URL =  AIS_PATH+"aisMain.xhtml" + REDIRECT;
    public final static String AIS_PD_URL = AIS_PATH + "product_design/productDesign.xhtml" + REDIRECT;
    public final static String AIS_RM_URL = AIS_PATH + "booking_class/viewFlightSchedule.xhtml" + REDIRECT;
    
    public final static String AFOS_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_BM_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_CR_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_MM_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_FCM_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    public final static String AFOS_CT_URL = AFOS_PATH + "afosMain.xhtml" + REDIRECT;
    
}
