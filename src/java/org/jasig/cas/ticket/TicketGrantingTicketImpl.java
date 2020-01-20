/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
@Entity
@Table(name = "TICKETGRANTINGTICKET")
public class TicketGrantingTicketImpl extends AbstractTicket implements TicketGrantingTicket {
  private static final long serialVersionUID = -5197946718924166491L;
  
  private static final Log LOG = LogFactory.getLog(org.jasig.cas.ticket.TicketGrantingTicketImpl.class);
  
  @Lob
  @Column(name = "AUTHENTICATION", nullable = false)
  private Authentication authentication;
  
  @Column(name = "EXPIRED", nullable = false)
  private Boolean expired = Boolean.valueOf(false);
  
  @Lob
  @Column(name = "SERVICES_GRANTED_ACCESS_TO", nullable = false)
  private final HashMap<String, Service> services = new HashMap<>();
  
  public TicketGrantingTicketImpl() {}
  
  public TicketGrantingTicketImpl(String id, org.jasig.cas.ticket.TicketGrantingTicketImpl ticketGrantingTicket, Authentication authentication, ExpirationPolicy policy) {
    super(id, ticketGrantingTicket, policy);
    Assert.notNull(authentication, "authentication cannot be null");
    this.authentication = authentication;
  }
  
  public TicketGrantingTicketImpl(String id, Authentication authentication, ExpirationPolicy policy) {
    this(id, null, authentication, policy);
  }
  
  public Authentication getAuthentication() {
    return this.authentication;
  }
  
  public synchronized ServiceTicket grantServiceTicket(String id, Service service, ExpirationPolicy expirationPolicy, boolean credentialsProvided) {
    ServiceTicketImpl serviceTicketImpl = new ServiceTicketImpl(id, this, service, (getCountOfUses() == 0 || credentialsProvided), expirationPolicy);
    updateState();
    List<Authentication> authentications = getChainedAuthentications();
    service.setPrincipal(((Authentication)authentications.get(authentications.size() - 1)).getPrincipal());
    this.services.put(id, service);
    return (ServiceTicket)serviceTicketImpl;
  }
  
  private void logOutOfServices() {
    for (Map.Entry<String, Service> entry : this.services.entrySet()) {
      if (!((Service)entry.getValue()).logOutOfService(entry.getKey()))
        LOG.warn("Logout message not sent to [" + ((Service)entry.getValue()).getId() + "]; Continuing processing..."); 
    } 
  }
  
  public boolean isRoot() {
    return (getGrantingTicket() == null);
  }
  
  public synchronized void expire() {
    this.expired = Boolean.valueOf(true);
    logOutOfServices();
  }
  
  public boolean isExpiredInternal() {
    return this.expired.booleanValue();
  }
  
  public List<Authentication> getChainedAuthentications() {
    List<Authentication> list = new ArrayList<>();
    if (getGrantingTicket() == null) {
      list.add(getAuthentication());
      return Collections.unmodifiableList(list);
    } 
    list.add(getAuthentication());
    list.addAll(getGrantingTicket().getChainedAuthentications());
    return Collections.unmodifiableList(list);
  }
  
  public final boolean equals(Object object) {
    if (object == null || !(object instanceof TicketGrantingTicket))
      return false; 
    Ticket ticket = (Ticket)object;
    return ticket.getId().equals(getId());
  }
  
  public int hashCode() {
    int hash = 3;
    return hash;
  }
}