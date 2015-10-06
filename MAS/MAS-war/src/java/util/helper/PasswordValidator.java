/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = value.toString();

        UIInput uiInputConfirmPassword = (UIInput) component.getAttributes().get("confirmPassword");
        String confirmPassword = uiInputConfirmPassword.getSubmittedValue().toString();

        if (!password.equals(confirmPassword)) {
            uiInputConfirmPassword.setValid(false);
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password must match confirm password."));
        }

        matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password must be 6 to 20 characters string with at least one digit, one upper case letter, one lower case letter and one special symbol (“@#$%”)."));
        }
    }
}
