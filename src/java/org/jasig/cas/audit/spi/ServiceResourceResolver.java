/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.audit.spi;

import org.aspectj.lang.JoinPoint;
import org.inspektr.audit.spi.AuditableResourceResolver;
import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public class ServiceResourceResolver implements AuditableResourceResolver{
    public String resolveFrom(JoinPoint joinPoint, Object retval) {
        Service service = (Service)joinPoint.getArgs()[1];
        return retval.toString() + " for " + service.getId();
    }

    public String resolveFrom(JoinPoint joinPoint, Exception ex) {
        Service service = (Service)joinPoint.getArgs()[1];
        return service.getId();
    }
}
