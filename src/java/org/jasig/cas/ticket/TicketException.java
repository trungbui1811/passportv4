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
public abstract class TicketException extends Exception{
    private static final long serialVersionUID = -6000583436059919480L;
    private String code;

    public TicketException(String code) {
        this.code = code;
    }

    public TicketException(String code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public final String getCode() {
        return (getCause() != null) ? getCause().toString() : this.code;
    }
}
