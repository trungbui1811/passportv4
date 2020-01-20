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
public class UnauthorizedServiceException extends RuntimeException{
    private static final long serialVersionUID = 3905807495715960369L;

    public UnauthorizedServiceException() {
      this("service.not.authorized");
    }

    public UnauthorizedServiceException(String message, Throwable cause) {
      super(message, cause);
    }

    public UnauthorizedServiceException(String message) {
      super(message);
    }
}
