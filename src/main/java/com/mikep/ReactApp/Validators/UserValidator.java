package com.mikep.ReactApp.Validators;

import com.mikep.ReactApp.Models.Client;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Client.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Client client = (Client)o;
//        if(client.getPassword().length() < 8) {
//            errors.rejectValue("password", "Length must be at least 8 characters");
//        }
        if(!client.getPassword().equals(client.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match");
        }
    }
}
