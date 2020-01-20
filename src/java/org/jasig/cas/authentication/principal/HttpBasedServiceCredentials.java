/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

import java.net.URL;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
public class HttpBasedServiceCredentials implements Credentials{
    private static final long serialVersionUID = 3904681574350991665L;
    private final URL callbackUrl;
    private final String callbackUrlAsString;

    public HttpBasedServiceCredentials(URL callbackUrl) {
      Assert.notNull(callbackUrl, "callbackUrl cannot be null");
      this.callbackUrl = callbackUrl;
      this.callbackUrlAsString = callbackUrl.toExternalForm();
    }

    public final URL getCallbackUrl() {
      return this.callbackUrl;
    }

    public final String toString() {
      return "[callbackUrl: " + this.callbackUrlAsString + "]";
    }

    public int hashCode() {
      int result = 1;
      result = 31 * result + ((this.callbackUrlAsString == null) ? 0 : this.callbackUrlAsString.hashCode());
      return result;
    }

    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj == null)
        return false; 
      if (getClass() != obj.getClass())
        return false; 
      org.jasig.cas.authentication.principal.HttpBasedServiceCredentials other = (org.jasig.cas.authentication.principal.HttpBasedServiceCredentials)obj;
      if (this.callbackUrlAsString == null) {
        if (other.callbackUrlAsString != null)
          return false; 
      } else if (!this.callbackUrlAsString.equals(other.callbackUrlAsString)) {
        return false;
      } 
      return true;
    }
}
