/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import mas.common.entity.SystemRole;
import mas.common.session.RoleSessionLocal;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
@FacesConverter("roleConverter")
public class RoleConverter implements Converter {
    
    @EJB
    private RoleSessionLocal roleSession;
    
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                //RoleController role = (RoleController) fc.getExternalContext().getRequestMap().get("roleController");
                //return role.getAllRoles().get(Integer.parseInt(value));
//                return roleSession.getAllRoles().get(Integer.parseInt(value)-1);
                return roleSession.getSystemRoleByName(value);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            } catch (NoSuchRoleException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", UserMsg.NO_SUCH_ROLE_ERROR));
            }
        } else {
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((SystemRole) object).getRoleName());
        } else {
            return null;
        }
    }

}
