/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.converter;

import ams.ars.entity.Seat;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.dcs.PassengerManager;

/**
 *
 * @author ChuningLiu
 */
@FacesConverter("seatConverter")
public class SeatConverter implements Converter{
    @Inject
    private PassengerManager passengerManager;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return passengerManager.getSeat();
            } catch (NullPointerException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid seat."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && !(value instanceof String)) {
            return String.valueOf(((Seat) value).getId());
        } else {
            return null;
        }
    }
    
}
