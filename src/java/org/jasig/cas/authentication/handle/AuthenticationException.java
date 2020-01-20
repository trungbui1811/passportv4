/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.handle;

/**
 *
 * @author TrungBH
 */
public class AuthenticationException extends Exception{
    private static final long serialVersionUID = 3906648604830611762L;

    private String code;

    public AuthenticationException(String code) {
      this.code = code;
    }

    public AuthenticationException(String code, Throwable throwable) {
      super(throwable);
      this.code = code;
    }

    public final String getCode() {
      return this.code;
    }

    public final String toString() {
      return getCode();
    }
}
