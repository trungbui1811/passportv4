/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import java.io.Serializable;

/**
 *
 * @author TrungBH
 */
public interface ExpirationPolicy extends Serializable{
    boolean isExpired(TicketState paramTicketState);
}
