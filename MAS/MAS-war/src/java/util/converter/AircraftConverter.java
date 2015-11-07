/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;
 
import ams.aps.entity.Aircraft;
import ams.ais.session.ProductDesignSessionLocal;
import ams.aps.util.exception.NoSuchAircraftException;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.application.MsgController;
 
@FacesConverter("aircraftConverter")
public class AircraftConverter implements Converter {

    @EJB
    private ProductDesignSessionLocal productDesignSession;
    @Inject
    MsgController msgController;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                return productDesignSession.getAircraftById(Long.parseLong(value));
            } catch (NoSuchAircraftException ex) {
                msgController.addErrorMessage(ex.getMessage());
                return null;
            }
        }
        else {
            return null;
        }
    }
 
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null && !(object instanceof String)) {
            return String.valueOf(((Aircraft) object).getAircraftId());
        }
        else {
            return null;
        }
    }   
}         
