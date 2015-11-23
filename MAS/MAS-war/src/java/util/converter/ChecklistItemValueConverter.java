/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Lewis
 */
@FacesConverter("checklistItemValueConverter")
public class ChecklistItemValueConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            if(value.equals("true")){
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
         if (value != null) {
             if((double) value == 1){
                 return "true";
             } else{
                 return "false";
             }
        } else {
            return "false";
        }
    }
    
}
