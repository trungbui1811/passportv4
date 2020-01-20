/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.services;

import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public interface RegisteredService {
    boolean isEnabled();

    boolean isAnonymousAccess();

    boolean isIgnoreAttributes();

    String[] getAllowedAttributes();

    boolean isAllowedToProxy();

    String getServiceId();

    long getId();

    String getName();

    String getTheme();

    boolean isSsoEnabled();

    String getDescription();

    int getEvaluationOrder();

    boolean matches(Service paramService);

    Object clone() throws CloneNotSupportedException;
}
