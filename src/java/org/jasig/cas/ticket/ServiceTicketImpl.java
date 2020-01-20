/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
@Entity
@Table(name = "SERVICETICKET")
public class ServiceTicketImpl extends AbstractTicket implements ServiceTicket {
  private static final long serialVersionUID = -4223319704861765405L;
  
  @Lob
  @Column(name = "SERVICE", nullable = false)
  private Service service;
  
  @Column(name = "FROM_NEW_LOGIN", nullable = false)
  private boolean fromNewLogin;
  
  @Column(name = "TICKET_ALREADY_GRANTED", nullable = false)
  private Boolean grantedTicketAlready = Boolean.valueOf(false);
  
  public ServiceTicketImpl() {}
  
  protected ServiceTicketImpl(String id, TicketGrantingTicketImpl ticket, Service service, boolean fromNewLogin, ExpirationPolicy policy) {
    super(id, ticket, policy);
    Assert.notNull(ticket, "ticket cannot be null");
    Assert.notNull(service, "service cannot be null");
    this.service = service;
    this.fromNewLogin = fromNewLogin;
  }
  
  public boolean isFromNewLogin() {
    return this.fromNewLogin;
  }
  
  public Service getService() {
    return this.service;
  }
  
  public boolean isValidFor(Service serviceToValidate) {
    updateState();
    return serviceToValidate.matches(this.service);
  }
  
  public TicketGrantingTicket grantTicketGrantingTicket(String id, Authentication authentication, ExpirationPolicy expirationPolicy) {
    synchronized (this) {
      if (this.grantedTicketAlready.booleanValue())
        throw new IllegalStateException("TicketGrantingTicket already generated for this ServiceTicket.  Cannot grant more than one TGT for ServiceTicket"); 
      this.grantedTicketAlready = Boolean.valueOf(true);
    } 
    return (TicketGrantingTicket)new TicketGrantingTicketImpl(id, (TicketGrantingTicketImpl)getGrantingTicket(), authentication, expirationPolicy);
  }
  
  public Authentication getAuthentication() {
    return null;
  }
  
  public final boolean equals(Object object) {
    if (object == null || !(object instanceof ServiceTicket))
      return false; 
    Ticket serviceTicket = (Ticket)object;
    return serviceTicket.getId().equals(getId());
  }
  
  public int hashCode() {
    return 15 * this.service.hashCode() ^ this.service.hashCode();
  }
}