/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import ams.ais.entity.Rule;
import ams.ais.session.ProductDesignSessionLocal;
import ams.ais.util.exception.NoSuchRuleException;
import ams.ais.util.helper.AisMsg;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import mas.common.entity.SystemRole;
import mas.common.util.exception.NoSuchRoleException;

/**
 *
 * @author Bowen
 */
@FacesConverter("ruleConverter")
public class RuleConverter implements Converter {
    @EJB
    private ProductDesignSessionLocal productDesignSession;
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            return productDesignSession.getRuleByName(value);
        } 
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null&& !(object instanceof String)) {
            return String.valueOf(((Rule) object).getName());
        } else {
            return null;
        }
    }

    
}
