/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.afos.entity.MaintCheckType;
import ams.afos.entity.MaintCrew;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.afos.MaintTaskManager;

/**
 *
 * @author Lewis
 */
@FacesConverter("maintCheckTypeConverter")
public class MaintCheckTypeConverter implements Converter {

    @Inject
    private MaintTaskManager maintTaskManager;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return maintTaskManager.getCheckTypeById(Long.parseLong(value));
            } catch (NullPointerException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && !(value instanceof String) && ((MaintCheckType) value).getMaintCheckTypeId()!= null) {
            return ((MaintCheckType) value).getMaintCheckTypeId().toString();
        } else {
            return null;
        }
    }
}
