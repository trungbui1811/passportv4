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
public class UnauthorizedSsoServiceException extends UnauthorizedServiceException{
    private static final long serialVersionUID = 8909291297815558561L;

    private static final String CODE = "service.not.authorized.sso";

    public UnauthorizedSsoServiceException() {
      this("service.not.authorized.sso");
    }

    public UnauthorizedSsoServiceException(String message, Throwable cause) {
      super(message, cause);
    }

    public UnauthorizedSsoServiceException(String message) {
      super(message);
    }
}
