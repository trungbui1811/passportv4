/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.bind;

import javax.servlet.http.HttpServletRequest;
import org.jasig.cas.authentication.principal.Credentials;

/**
 *
 * @author TrungBH
 */
public interface CredentialsBinder {
    void bind(HttpServletRequest paramHttpServletRequest, Credentials paramCredentials);

    boolean supports(Class<?> paramClass);
}
