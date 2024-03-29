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
import mas.common.entity.SystemUser;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
@FacesConverter("userConverter")
public class UserConverter implements Converter {
    
    @EJB
    private SystemUserSessionLocal systemUserSession;
    
   
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return systemUserSession.getSystemUserByName(value);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            } catch (NoSuchUsernameException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", UserMsg.NO_SUCH_USERNAME_ERROR));
            }
        } else {
            return null;
        }
    }

   
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null&& !(object instanceof String)) {
            return String.valueOf(((SystemUser) object).getUsername());
        } else {
            return null;
        }
    }

}
