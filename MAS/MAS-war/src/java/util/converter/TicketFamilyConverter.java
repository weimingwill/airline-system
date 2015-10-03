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
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Bowen
 */

@FacesConverter("ticketFamilyConverter")
public class TicketFamilyConverter implements Converter {
   @EJB
    private TicketFamilySessionLocal ticketFamilySession;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            return ticketFamilySession.getTicketFamilyById(Long.parseLong(value));
        } 
        else {
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
