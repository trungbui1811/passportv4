/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas;

import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.validation.Assertion;

/**
 *
 * @author TrungBH
 */
public interface CentralAuthenticationService {
    String createTicketGrantingTicket(Credentials paramCredentials) throws TicketException;
    String grantServiceTicket(String paramString, Service paramService) throws TicketException;
    String grantServiceTicket(String paramString, Service paramService, Credentials paramCredentials) throws TicketException;
    Assertion validateServiceTicket(String paramString, Service paramService) throws TicketException;
    void destroyTicketGrantingTicket(String paramString);
    String delegateTicketGrantingTicket(String paramString, Credentials paramCredentials) throws TicketException;
}
