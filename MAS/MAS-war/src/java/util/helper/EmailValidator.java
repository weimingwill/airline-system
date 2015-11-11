/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author winga_000
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    public EmailValidator() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = value.toString();

        UIInput uiInputConfirmPassword = (UIInput) component.getAttributes().get("confirmEmail");
        String confirmPassword = uiInputConfirmPassword.getSubmittedValue().toString();

        if (!email.equals(confirmPassword)) {
            uiInputConfirmPassword.setValid(false);
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email must match confirm email."));
        }

    }
}
