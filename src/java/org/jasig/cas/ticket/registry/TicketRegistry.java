/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket.registry;

import java.util.Collection;
import org.jasig.cas.ticket.Ticket;

/**
 *
 * @author TrungBH
 */
public interface TicketRegistry {
    void addTicket(Ticket paramTicket);
  
    Ticket getTicket(String paramString, Class<? extends Ticket> paramClass);

    Ticket getTicket(String paramString);

    boolean deleteTicket(String paramString);

    Collection<Ticket> getTickets();
}
