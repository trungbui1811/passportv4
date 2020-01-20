/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import java.util.List;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public interface TicketGrantingTicket extends Ticket{
    public static final String PREFIX = "TGT";

    Authentication getAuthentication();

    ServiceTicket grantServiceTicket(String paramString, Service paramService, ExpirationPolicy paramExpirationPolicy, boolean paramBoolean);

    void expire();

    boolean isRoot();

    List<Authentication> getChainedAuthentications();
}
