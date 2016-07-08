/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.aps.entity.City;
import ams.crm.entity.CustomerList;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import managedbean.aps.RouteController;
import managedbean.crm.CampaignManager;

/**
 *
 * @author ChuningLiu
 */
@FacesConverter("customerListConverter")
public class CustomerListConverter implements Converter {

    @Inject
    private CampaignManager campaignController;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return campaignController.getCustomerListById(Long.parseLong(value));
            } catch (NullPointerException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && !(value instanceof String) && ((CustomerList)value).getId()!=null) {
            return ((CustomerList) value).getId().toString();
        } else {
            return null;
        }
    }

}
