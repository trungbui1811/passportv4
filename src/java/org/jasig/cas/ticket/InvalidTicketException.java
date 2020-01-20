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
public class InvalidTicketException extends TicketException{
    private static final long serialVersionUID = 3256723974594508849L;

    private static final String CODE = "INVALID_TICKET";

    public InvalidTicketException() {
      super("INVALID_TICKET");
    }

    public InvalidTicketException(Throwable throwable) {
      super("INVALID_TICKET", throwable);
    }
}
