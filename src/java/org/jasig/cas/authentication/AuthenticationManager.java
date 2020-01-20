/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication;

import org.jasig.cas.authentication.handle.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;

/**
 *
 * @author TrungBH
 */
public interface AuthenticationManager {
    public static final String AUTHENTICATION_METHOD_ATTRIBUTE = "authenticationMethod";
  
    Authentication authenticate(Credentials paramCredentials) throws AuthenticationException;
}
