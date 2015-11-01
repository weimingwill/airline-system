/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author winga_000
 */
@Named(value = "navigationController")
@SessionScoped
public class NavigationController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";
    private final String COMMON_URL = "/views/internal/secured/common/";
    private final String APS_URL = "/views/internal/secured/aps/";
    private final String AIS_URL = "/views/internal/secured/ais/";
    

    
    public NavigationController() {
    }

    public String toLogin() {
        return COMMON_URL + "users/login.xhtml";
    }

    public String redirectToLogin() {
        return COMMON_URL + "users/login.xhtml" + REDIRECT;
    }

    public String redirectToHome() {
        return "/index.xhtml";
    }

    //Message
    public String toSendMessage() {
        return COMMON_URL + "messages/sendMessage.xhtml";
    }

    public String toViewMessages() {
        return COMMON_URL + "messages/viewMessages.xhtml";
    }

    public String redirectToSendMessages() {
        return COMMON_URL + "messages/sendMessage.xhtml" + REDIRECT;
    }

    public String redirectToViewMessages() {
        return COMMON_URL + "messages/viewMessages.xhtml" + REDIRECT;
    }

    //Password reset
    public String toPasswordReset() {
        return "/views/unsecured/common/users/resetPassword.xhtml";
    }

    public String toPasswordResetSendEmail() {
        return "/views/unsecured/common/users/resetPasswordSendEmail.xhtml";
    }

    public String redirectToPasswordResetSendEmail() {
        return "/views/unsecured/common/users/resetPasswordSendEmail.xhtml" + REDIRECT;
    }

    public String toUnsecuredUsersFolder() {
        return "http://localhost:8080/MAS-war/views/unsecured/common/users/";
    }

    //Access control
    public String redirectToWorkspace() {
        return COMMON_URL + "users/userWorkspace.xhtml" + REDIRECT;
    }

    public String redirectToAdminWorkspace() {
        return COMMON_URL + "users/adminWorkspace.xhtml" + REDIRECT;
    }

    //User
    public String toCreateUser() {
        return COMMON_URL + "users/createUser.xhtml";
    }

    public String redirectToCreateUser() {
        return COMMON_URL + "users/createUser.xhtml" + REDIRECT;
    }

    public String toAssignUserRole() {
        return COMMON_URL + "users/assignUserRole.xhtml";
    }

    public String redirectToAssignUserRoles() {
        return COMMON_URL + "users/assignUserRoles.xhtml" + REDIRECT;
    }

    public String toViewAllUsers() {
        return COMMON_URL + "users/viewAllUsers.xhtml";
    }

    public String redirectToViewAllUsers() {
        return COMMON_URL + "users/viewAllUsers.xhtml" + REDIRECT;
    }

    public String redirectToViewUserProfile() {
        return COMMON_URL + "users/viewProfile.xhtml" + REDIRECT;
    }

    public String toEditUserProfile() {
        return COMMON_URL + "users/editProfile.xhtml";
    }

    public String redirectToEditUserProfile() {
        return COMMON_URL + "users/editProfile.xhtml" + REDIRECT;
    }

    public String toChangePassword() {
        return COMMON_URL + "users/changePassword.xhtml";
    }

    public String redirectToChangePassword() {
        return COMMON_URL + "users/changePassword.xhtml" + REDIRECT;
    }

    //Role
    public String toCreateRole() {
        return COMMON_URL + "roles/createRole.xhtml";
    }

    public String redirectToCreateRole() {
        return COMMON_URL + "roles/createRole.xhtml" + REDIRECT;
    }

    public String toViewAllRoles() {
        return COMMON_URL + "roles/viewAllRoles.xhtml";
    }

    public String redirectToViewAllRoles() {
        return COMMON_URL + "roles/viewAllRoles.xhtml" + REDIRECT;
    }

    public String toGrantRolePermissions() {
        return COMMON_URL + "roles/grantRolePermissions.xhtml";
    }

    public String redirectToGrantRolePermissions() {
        return COMMON_URL + "roles/grantRolePermissions.xhtml" + REDIRECT;
    }

    //Permission
    public String toCreatePermission() {
        return COMMON_URL + "permissions/createPermission.xhtml";
    }

    public String redirectToCreatePermission() {
        return COMMON_URL + "permissions/createPermission.xhtml?" + REDIRECT;
    }

    public String toViewAllPermissions() {
        return COMMON_URL + "permissions/viewAllPermissions.xhtml";
    }

    public String redirectToViewAllPermission() {
        return COMMON_URL + "permissions/viewAllPermissions.xhtml" + REDIRECT;
    }

    //AIS
    public String redirectToAIS() {
        return AIS_URL + "aisMain.xhtml" + REDIRECT;
    }

    //Product Design
    public String redirectToCreateCabinClass() {
        return AIS_URL + "product_design/createCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToViewAllCabinClass() {
        return AIS_URL + "product_design/viewAllCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToDeleteCabinClass() {
        return AIS_URL + "product_design/deleteCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToEditCabinClass() {
        return AIS_URL + "product_design/editCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToCreateRule() {
        return AIS_URL + "product_design/createRule.xhtml" + REDIRECT;
    }

    public String redirectToViewAllRules() {
        return AIS_URL + "product_design/viewAllRule.xhtml" + REDIRECT;
    }

    public String redirectToDeleteRule() {
        return AIS_URL + "product_design/deleteRule.xhtml" + REDIRECT;
    }

    public String redirectToEditRule() {
        return AIS_URL + "product_design/editRule.xhtml" + REDIRECT;
    }

    public String redirectToCreateTicketFamily() {
        return AIS_URL + "product_design/createTicketFamily.xhtml" + REDIRECT;
    }

    public String redirectToViewAllTicketFamily() {
        return AIS_URL + "product_design/viewAllTicketFamily.xhtml" + REDIRECT;
    }

    public String redirectToDeleteTicketFamily() {
        return AIS_URL + "product_design/deleteTicketFamily.xhtml" + REDIRECT;
    }

    public String redirectToEditTicketFamily() {
        return AIS_URL + "product_design/editTicketFamily.xhtml" + REDIRECT;
    }

    //Pricing
    public String redirectToCreateBookingClass() {
        return AIS_URL + "booking_class/createBookingClass.xhtml" + REDIRECT;
    }
    public String redirectToViewAllBookingClass(){
        return AIS_URL + "booking_class/viewBookingClass.xhtml" + REDIRECT;
    }
    public String redirectToDeleteBookingClass() {
        return AIS_URL + "booking_class/deleteBookingClass.xhtml" + REDIRECT;
    }

    public String redirectToPricing() {
        return AIS_URL + "pricing/pricing.xhtml" + REDIRECT;
    }

    public String redirectToAssignFlightScheduleBookingClass() {
        return AIS_URL + "booking_class/assignFlightScheduleBookingClasses.xhtml" + REDIRECT;
    }

    public String redirectToViewFlightSchedule() {
        return AIS_URL + "booking_class/viewFlightSchedule.xhtml" + REDIRECT;
    }

    public String redirectToSeatAllocation() {
        return AIS_URL + "booking_class/seatAllocation.xhtml" + REDIRECT;
    }

    public String redirectToProductDesign() {
        return AIS_URL + "product_design/productDesign.xhtml" + REDIRECT;
    }

    public String redirectToPriceBookingClasses() {
        return AIS_URL + "booking_class/priceBookingClasses.xhtml" + REDIRECT;
    }

    //Yield Management 
    public String redirectToYieldManagement() {
        return AIS_URL + "yield_management/yieldManagement.xhtml" + REDIRECT;
    }

    public String redirectToReallocationBookingClassSeats() {
        return AIS_URL + "yield_management/reallocateBookingClassSeats.xhtml" + REDIRECT;
    }

    public String redirectToUpdateYieldManagementModel() {
        return AIS_URL + "yield_management/updateYieldManagementModel.xhtml" + REDIRECT;
    }

    public String redirectToViewSeatsReallocationHistroy() {
        return AIS_URL + "yield_management/viewSeatsReallocationHistory.xhtml" + REDIRECT;
    }
    
    public String redirectToAddCheckPoint(){
        return AIS_URL + "yield_management/addCheckPoint.xhtml" + REDIRECT;
    }

    //Airline Planning System
    //Fleet Planning Module
    public String redirectToAPS() {
        return APS_URL + "apsMain.xhtml" + REDIRECT;
    }

    public String toViewAircraftModel() {
        return APS_URL + "fleet/viewAircraftModel.xhtml" + REDIRECT;
    }

    public String toAddNewAircraft() {
        return APS_URL + "fleet/addNewAircraft.xhtml" + REDIRECT;
    }

    public String toViewFleet() {
        return APS_URL + "fleet/viewFleet.xhtml" + REDIRECT;
    }

    public String toRetireAicraft() {
        return APS_URL + "fleet/retireAircraft.xhtml" + REDIRECT;
    }

    public String toViewRetiredFleet() {
        return APS_URL + "fleet/viewRetiredFleet.xhtml" + REDIRECT;
    }

    // Route Planning Module
    public String toAddHub() {
        return APS_URL + "route/addHub.xhtml" + REDIRECT;
    }

    public String toCancelHub() {
        return APS_URL + "route/cancelHub.xhtml" + REDIRECT;
    }

    public String toAddRoute() {
        return APS_URL + "route/addRoute.xhtml" + REDIRECT;
    }

    public String toCheckRoute() {
        return APS_URL + "route/planRoute.xhtml" + REDIRECT;
    }

    public String toGenerateRoute() {
        return APS_URL + "route/generateRoute.xhtml";
    }

    public String toCustomizeRoute() {
        return APS_URL + "route/customizeRoute.xhtml";
    }

    public String toViewRoutes() {
        return APS_URL + "route/viewRoutes.xhtml" + REDIRECT;
    }

    public String toViewObsoleteRoutes() {
        return APS_URL + "route/viewObsoleteRoute.xhtml" + REDIRECT;
    }

    public String toAddAirport() {
        return APS_URL + "route/addAirport.xhtml" + REDIRECT;
    }

    // Flight Scheduling
    public String toCreateFlight() {
        return APS_URL + "flight_schedule/createFlight.xhtml" + REDIRECT;
    }

    public String toFleetAssignment() {
        return APS_URL + "flight_schedule/fleetAssignment.xhtml";
    }

    public String toFreqPlanning() {
        return APS_URL + "flight_schedule/freqPlanning.xhtml";
    }

    public String toConfirmFlight() {
        return APS_URL + "flight_schedule/confirmFlight.xhtml";
    }

    public String redirectToSelectFlight() {
        return APS_URL + "flight_schedule/selectFlight.xhtml" + REDIRECT;
    }

    public String toSelectFlight() {
        return APS_URL + "flight_schedule/selectFlight.xhtml";
    }

    public String toScheduleFlight() {
        return APS_URL + "flight_schedule/scheduleFlight.xhtml";
    }

    public String redirectToScheduleFlight() {
        return APS_URL + "flight_schedule/scheduleFlight.xhtml" + REDIRECT;
    }

    public String redirectToConfirmScheduleFlight() {
        return APS_URL + "flight_schedule/confirmFlightSchedule.xhtml" + REDIRECT;
    }

    public String toConfirmScheduleFlight() {
        return APS_URL + "flight_schedule/confirmFlightSchedule.xhtml";
    }
    
    public String toViewFlightSchedule() {
        return APS_URL + "flight_schedule/viewFlightSchedule.xhtml" + REDIRECT;
    }
    
    public String redirectToCurrentPage() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = request.getRequestURI();
        uri = uri.substring(uri.indexOf('/', 1));
        return uri + REDIRECT;
    }

}
