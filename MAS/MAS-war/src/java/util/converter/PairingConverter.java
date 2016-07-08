/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.afos.entity.Pairing;
import ams.aps.util.exception.NoSuchAircraftException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.afos.FlightCrewBacking;

/**
 *
 * @author Lewis
 */
@FacesConverter("pairingConverter")
public class PairingConverter implements Converter {

    @Inject
    private FlightCrewBacking flightCrewBacking;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            return flightCrewBacking.getPairingById(Long.parseLong(value));
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null && !(object instanceof String)) {
            return String.valueOf(((Pairing) object).getPairingId());
        } else {
            return null;
        }
    }
}
