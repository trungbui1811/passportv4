/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
@MappedSuperclass
public abstract class AbstractTicket implements Ticket, TicketState {
  @Lob
  @Column(name = "EXPIRATION_POLICY", nullable = false)
  private ExpirationPolicy expirationPolicy;
  
  @Id
  @Column(name = "ID", nullable = false)
  private String id;
  
  @ManyToOne
  private TicketGrantingTicketImpl ticketGrantingTicket;
  
  @Column(name = "LAST_TIME_USED")
  private long lastTimeUsed;
  
  @Column(name = "PREVIOUS_LAST_TIME_USED")
  private long previousLastTimeUsed;
  
  @Column(name = "CREATION_TIME")
  private long creationTime;
  
  @Column(name = "NUMBER_OF_TIMES_USED")
  private int countOfUses;
  
  protected AbstractTicket() {}
  
  public AbstractTicket(String id, TicketGrantingTicketImpl ticket, ExpirationPolicy expirationPolicy) {
    Assert.notNull(expirationPolicy, "expirationPolicy cannot be null");
    Assert.notNull(id, "id cannot be null");
    this.id = id;
    this.creationTime = System.currentTimeMillis();
    this.lastTimeUsed = System.currentTimeMillis();
    this.expirationPolicy = expirationPolicy;
    this.ticketGrantingTicket = ticket;
  }
  
  public final String getId() {
    return this.id;
  }
  
  protected final void updateState() {
    this.previousLastTimeUsed = this.lastTimeUsed;
    this.lastTimeUsed = System.currentTimeMillis();
    this.countOfUses++;
  }
  
  public final int getCountOfUses() {
    return this.countOfUses;
  }
  
  public final long getCreationTime() {
    return this.creationTime;
  }
  
  public final TicketGrantingTicket getGrantingTicket() {
    return (TicketGrantingTicket)this.ticketGrantingTicket;
  }
  
  public final long getLastTimeUsed() {
    return this.lastTimeUsed;
  }
  
  public final long getPreviousTimeUsed() {
    return this.previousLastTimeUsed;
  }
  
  public final boolean isExpired() {
    return (this.expirationPolicy.isExpired(this) || (getGrantingTicket() != null && getGrantingTicket().isExpired()) || isExpiredInternal());
  }
  
  protected boolean isExpiredInternal() {
    return false;
  }
  
  public int hashCode() {
    return 0x22 ^ getId().hashCode();
  }
  
  public boolean equals(Object object) {
    return true;
  }
  
  public final String toString() {
    return this.id;
  }
}