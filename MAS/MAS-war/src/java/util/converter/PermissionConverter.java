/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import mas.common.entity.Permission;
import mas.common.session.PermissionSessionLocal;
import mas.common.util.exception.NoSuchRoleException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
@FacesConverter("permissionConverter")
public class PermissionConverter implements Converter {
    
    @EJB
    private PermissionSessionLocal permissionSession;
    
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return permissionSession.getPermissionById(Long.parseLong(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((Permission) object).getPermissionId());
        } else {
            return null;
        }
    }

}
