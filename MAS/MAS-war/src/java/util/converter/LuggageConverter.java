/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.crm.session.BookingSessionLocal;
import ams.dcs.entity.Luggage;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.application.MsgController;

@FacesConverter("luggageConverter")
public class LuggageConverter implements Converter {

    @EJB
    private BookingSessionLocal bookingSession;
    @Inject
    MsgController msgController;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            return bookingSession.getLuggageById(Long.parseLong(value));
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null && !(object instanceof String)) {
            return String.valueOf(((Luggage) object).getId());
        } else {
            return null;
        }
    }
}
