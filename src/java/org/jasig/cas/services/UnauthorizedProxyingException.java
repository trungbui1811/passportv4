/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.services;

/**
 *
 * @author TrungBH
 */
public class UnauthorizedProxyingException extends UnauthorizedServiceException{
    private static final long serialVersionUID = -7307803750894078575L;

    private static final String CODE = "service.not.authorized.proxy";

    public UnauthorizedProxyingException() {
      super("service.not.authorized.proxy");
    }

    public UnauthorizedProxyingException(String message, Throwable cause) {
      super(message, cause);
    }

    public UnauthorizedProxyingException(String message) {
      super(message);
    }
}
