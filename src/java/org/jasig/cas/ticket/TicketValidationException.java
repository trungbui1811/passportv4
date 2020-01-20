/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.ticket;

import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public class TicketValidationException extends TicketException{
    private static final long serialVersionUID = 3257004341537093175L;
    private final Service service;

    public TicketValidationException(Service service) {
      super("INVALID_SERVICE");
      this.service = service;
    }

    public Service getOriginalService() {
      return this.service;
    }
}
