/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;
 
import ams.aps.entity.AircraftType;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.aps.FleetController;
 
@FacesConverter("aircraftModelConverter")
public class AircraftModelConverter implements Converter {

    @Inject
    FleetController fleetController;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            return fleetController.getAircraftModelById(Long.parseLong(value));
        }
        else {
            return null;
        }
    }
 
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((AircraftType) object).getId());
        }
        else {
            return null;
        }
    }   
}         
