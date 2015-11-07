/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;


import ams.ais.entity.TicketFamily;
import ams.ais.session.TicketFamilySessionLocal;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import ams.ais.entity.TicketFamily;
import ams.ais.session.TicketFamilySessionLocal;
import ams.ais.util.exception.NoSuchTicketFamilyException;
import ams.ais.util.helper.AisMsg;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Bowen
 */


@FacesConverter("ticketFamilyConverter")
public class TicketFamilyConverter implements Converter {
    
    @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return ticketFamilySession.getTicketFamilyById(Long.parseLong(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            } catch (NoSuchTicketFamilyException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", AisMsg.EXIST_SUCH_TICKET_FAMILY_ERROR));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null && !(object instanceof String)) {
            return String.valueOf(((TicketFamily)object).getTicketFamilyId());
        } else {
            return null;
        }
    } 

}
