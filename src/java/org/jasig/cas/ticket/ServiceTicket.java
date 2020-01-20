/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public interface ServiceTicket extends Ticket{
  public static final String PREFIX = "ST";
  
  Service getService();
  
  boolean isFromNewLogin();
  
  boolean isValidFor(Service paramService);
  
  TicketGrantingTicket grantTicketGrantingTicket(String paramString, Authentication paramAuthentication, ExpirationPolicy paramExpirationPolicy);
}
