/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.converter;

import ams.ais.entity.BookingClass;
import ams.ais.session.SeatReallocationSession;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.aps.entity.Airport;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@FacesConverter("bookingClassConverter")
public class BookingClassConverter implements Converter {
    @Inject
    private SeatReallocationController seatReallocationController;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                Object obj = seatReallocationController.getBookingClassbyName(value);
                return obj;
            } catch (NoSuchBookingClassException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid booking class."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && !(value instanceof String)) {
            return ((BookingClass)value).getName();
        } else {
            return null;
        }
    }
}
