/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

/**
 *
 * @author TrungBH
 */
public interface Ticket {
    String getId();
  
    boolean isExpired();

    TicketGrantingTicket getGrantingTicket();

    long getCreationTime();

    int getCountOfUses();
}
