/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.ais.entity.TicketFamily;
import ams.aps.entity.City;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.ais.SeatReallocationController;
import managedbean.aps.RouteController;

/**
 *
 * @author ChuningLiu
 */
@FacesConverter("ticketFamilyConverter")
public class TicketFamilyConverter implements Converter {

    @Inject
    private SeatReallocationController seatReallocationController;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return seatReallocationController.getTicketFamilybyName(value);
            } catch (NullPointerException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && value != "null" && !(value instanceof String)) {
            return ((TicketFamily) value).getTicketFamilyId().toString();
        } else {
            return null;
        }
    }

}
