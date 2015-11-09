

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;
 
import ams.ais.entity.FlightScheduleBookingClass;
import ams.ais.session.RevMgmtSessionLocal;
import ams.ais.util.exception.NoSuchBookingClassException;
import ams.ais.util.helper.AisMsg;
import ams.aps.util.exception.NoSuchFlightScheduleBookingClassException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

 
@FacesConverter("flightSchedBookingClsConverter")
public class FlightSchedBookingClsConverter implements Converter {
    
    @EJB
    private RevMgmtSessionLocal revMgmtSession;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                Long bookingClsId = Long.parseLong(value.substring(value.indexOf("=") + 1, value.indexOf(" ]")));
                Long flightSchedId = Long.parseLong(value.substring(value.lastIndexOf("=") + 1, value.lastIndexOf(" ]")));
                return revMgmtSession.getFlightScheduleBookingClass(flightSchedId, bookingClsId);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid flightschedule booking class."));
            } catch (NoSuchFlightScheduleBookingClassException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ex.getMessage()));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null && !(object instanceof String)) {
            return String.valueOf(((FlightScheduleBookingClass)object).getFlightScheduleBookingClassId());
        } else {
            return null;
        }
    }
    
   
}
      
