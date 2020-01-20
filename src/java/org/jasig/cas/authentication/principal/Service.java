/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

/**
 *
 * @author TrungBH
 */
public interface Service extends Principal{
    void setPrincipal(Principal paramPrincipal);
    boolean logOutOfService(String paramString);
    boolean matches(Service paramService);
}
