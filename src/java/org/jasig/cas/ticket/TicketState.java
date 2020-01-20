/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import org.jasig.cas.authentication.Authentication;

/**
 *
 * @author TrungBH
 */
public interface TicketState {
    int getCountOfUses();
  
    long getLastTimeUsed();

    long getPreviousTimeUsed();

    long getCreationTime();

    Authentication getAuthentication();
}
