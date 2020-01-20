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
public class TicketCreationException extends TicketException{
    private static final long serialVersionUID = 5501212207531289993L;

    private static final String CODE = "CREATION_ERROR";

    public TicketCreationException() {
      super("CREATION_ERROR");
    }

    public TicketCreationException(Throwable throwable) {
      super("CREATION_ERROR", throwable);
    }
}
