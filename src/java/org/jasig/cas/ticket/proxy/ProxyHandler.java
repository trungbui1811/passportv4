/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket.proxy;

import org.jasig.cas.authentication.principal.Credentials;

/**
 *
 * @author TrungBH
 */
public interface ProxyHandler {
    String handle(Credentials paramCredentials, String paramString);
}
