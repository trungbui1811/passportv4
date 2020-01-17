/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.validation;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author TrungBH
 */
public final class UsernamePasswordCredentialsValidator implements Validator{
//    public boolean supports(Class<?> clazz) {
//        return UsernamePasswordCredentials.class.isAssignableFrom(clazz);
//    }

    public void validate(Object o, Errors errors) {}

    @Override
    public boolean supports(Class type) {
        return UsernamePasswordCredentials.class.isAssignableFrom(type);
    }
}
