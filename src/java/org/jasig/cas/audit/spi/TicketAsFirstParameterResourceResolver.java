/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.audit.spi;

import org.aspectj.lang.JoinPoint;
import org.inspektr.audit.spi.AuditableResourceResolver;

/**
 *
 * @author TrungBH
 */
public final class TicketAsFirstParameterResourceResolver implements AuditableResourceResolver{
    public String resolveFrom(JoinPoint joinPoint, Exception exception) {
        return joinPoint.getArgs()[0].toString();
    }

    public String resolveFrom(JoinPoint joinPoint, Object object) {
        return joinPoint.getArgs()[0].toString();
    }
}
