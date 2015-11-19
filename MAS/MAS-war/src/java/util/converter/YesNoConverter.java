/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * @author Bowen
 */
public class YesNoConverter implements javax.faces.convert.Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 == null) {
            return null;
        } else {
            if ("Yes".equalsIgnoreCase(arg2)) {
                return Boolean.TRUE;
            } else if ("No".equalsIgnoreCase(arg2)) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        if (arg2 == null) {
            return "";
        }
        if (((Boolean) arg2)) {
            return "Yes";
        } else {
            return "No";
        }
    }
}
