/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;


import ams.ais.entity.CabinClass;
import ams.ais.session.CabinClassSessionLocal;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Bowen
 */

@FacesConverter("cabinClassConverter")
public class CabinClassConverter implements Converter {
    @EJB
    private CabinClassSessionLocal cabinClassSession;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            return cabinClassSession.getCabinClassByName(value);
        } 
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null&& !(object instanceof String)) {
            return String.valueOf(((CabinClass) object).getName());
        } else {
            return null;
        }
    }

}