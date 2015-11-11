/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.ais.entity.CabinClass;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.util.exception.NoSuchCabinClassException;
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


@FacesConverter("cabinClassConverter")
public class CabinClassConverter implements Converter {

    @EJB
    private ProductDesignSessionLocal productDesignSession;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return productDesignSession.getCabinClassByName(value);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            } catch (NoSuchCabinClassException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", AisMsg.NO_SUCH_CABIN_CLASS_ERROR));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null && !(object instanceof String)) {
            return String.valueOf(((CabinClass) object).getName());
        } else {
            return null;
        }
    }
}
