/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.application;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author winga_000
 */
@Named(value = "navigationController")
@SessionScoped
public class NavigationController implements Serializable {

    private final String REDIRECT = "?faces-redirect=true";

    public NavigationController() {
    }

    public String toLogin() {
        return "/views/secured/common/users/login.xhtml";
    }

    public String redirectToHome() {
        return "/index.xhtml";
    }

    //Message
    public String toSendMessage() {
        return "/views/secured/common/messages/sendMessage.xhtml";
    }

    public String toViewMessages() {
        return "/views/secured/common/messages/viewMessages.xhtml";
    }

    public String redirectToSendMessages() {
        return "/views/secured/common/messages/sendMessage.xhtml" + REDIRECT;
    }

    public String redirectToViewMessages() {
        return "/views/secured/common/messages/viewMessages.xhtml" + REDIRECT;
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
        return "/views/secured/common/users/userWorkspace.xhtml" + REDIRECT;
    }

    public String redirectToAdminWorkspace() {
        return "/views/secured/common/users/adminWorkspace.xhtml" + REDIRECT;
    }

    //User
    public String toCreateUser() {
        return "/views/secured/common/users/createUser.xhtml";
    }

    public String redirectToCreateUser() {
        return "/views/secured/common/users/createUser.xhtml" + REDIRECT;
    }

    public String toAssignUserRole() {
        return "/views/secured/common/users/assignUserRole.xhtml";
    }

    public String redirectToAssignUserRoles() {
        return "/views/secured/common/users/assignUserRoles.xhtml" + REDIRECT;
    }

    public String toViewAllUsers() {
        return "/views/secured/common/users/viewAllUsers.xhtml";
    }

    public String redirectToViewAllUsers() {
        return "/views/secured/common/users/viewAllUsers.xhtml" + REDIRECT;
    }

    public String redirectToViewUserProfile() {
        return "/views/secured/common/users/viewProfile.xhtml" + REDIRECT;
    }

    public String toEditUserProfile() {
        return "/views/secured/common/users/editProfile.xhtml";
    }

    public String redirectToEditUserProfile() {
        return "/views/secured/common/users/editProfile.xhtml" + REDIRECT;
    }

    public String toChangePassword() {
        return "/views/secured/common/users/changePassword.xhtml";
    }

    public String redirectToChangePassword() {
        return "/views/secured/common/users/changePassword.xhtml" + REDIRECT;
    }

    //Role
    public String toCreateRole() {
        return "/views/secured/common/roles/createRole.xhtml";
    }

    public String redirectToCreateRole() {
        return "/views/secured/common/roles/createRole.xhtml" + REDIRECT;
    }

    public String toViewAllRoles() {
        return "/views/secured/common/roles/viewAllRoles.xhtml";
    }

    public String redirectToViewAllRoles() {
        return "/views/secured/common/roles/viewAllRoles.xhtml" + REDIRECT;
    }

    public String toGrantRolePermissions() {
        return "/views/secured/common/roles/grantRolePermissions.xhtml";
    }

    public String redirectToGrantRolePermissions() {
        return "/views/secured/common/roles/grantRolePermissions.xhtml" + REDIRECT;
    }

    //Permission
    public String toCreatePermission() {
        return "/views/secured/common/permissions/createPermission.xhtml";
    }

    public String redirectToCreatePermission() {
        return "/views/secured/common/permissions/createPermission.xhtml?" + REDIRECT;
    }

    public String toViewAllPermissions() {
        return "/views/secured/common/permissions/viewAllPermissions.xhtml";
    }

    public String redirectToViewAllPermission() {
        return "/views/secured/common/permissions/viewAllPermissions.xhtml" + REDIRECT;
    }

    //AIS
    
    public String redirectToAIS() {
        return "/views/secured/ais/aisMain.xhtml" + REDIRECT;
    }
    
        //Product Design

    public String redirectToCreateCabinClass() {
        return "/views/secured/ais/product_design/createCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToViewAllCabinClass() {
        return "/views/secured/ais/product_design/viewAllCabinClass.xhtml" + REDIRECT;
    }

    public String redirectToDeleteCabinClass() {
        return "/views/secured/ais/product_design/deleteCabinClass.xhtml" + REDIRECT;
    }

        //Pricing

    public String redirectToCreateBookingClass() {
        return "/views/secured/ais/booking_class/createBookingClass.xhtml" + REDIRECT;
    }

    public String redirectToDeleteBookingClass() {
        return "/views/secured/ais/booking_class/deleteBookingClass.xhtml" + REDIRECT;
    }

    public String redirectToPricing() {
        return "/views/secured/ais/pricing/pricing.xhtml" + REDIRECT;
    }

    
    
    
    //APS
    public String redirectToAPS() {
        return "/views/secured/aps/apsMain.xhtml?faces-redirect=true";
    }

    public String toViewAircraftModel() {
        return "/views/secured/aps/fleet/viewAircraftModel.xhtml";
    }

    public String toAddNewAircraft() {
        return "/views/secured/aps/fleet/addNewAircraft.xhtml";
    }

    public String redirectToAddHub() {
        return "/views/secured/aps/route/addHub.xhtml" + REDIRECT;
    }

    public String redirectToCancelHub() {
        return "/views/secured/aps/route/cancelHub.xhtml" + REDIRECT;

    }

    public String redirectToAddRoute() {
        return "/views/secured/aps/route/addRoute.xhtml" + REDIRECT;

    }

    public String redirectToViewRoutes() {
        return "/views/secured/aps/route/viewRoutes.xhtml" + REDIRECT;
    }
    
    public String redirectToViewFlightSchedulings() {
        return "/views/secured/aps/flight_scheduling/viewFlightSchedulings.xhtml" + REDIRECT;
    }
}
