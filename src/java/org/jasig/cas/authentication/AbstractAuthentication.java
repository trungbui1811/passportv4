/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication;

import java.util.Map;
import org.jasig.cas.authentication.principal.Principal;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
public abstract class AbstractAuthentication implements Authentication{
    private final Principal principal;

    private final Map<String, Object> attributes;

    public AbstractAuthentication(Principal principal, Map<String, Object> attributes) {
      Assert.notNull(principal, "principal cannot be null");
      Assert.notNull(attributes, "attributes cannot be null");
      this.principal = principal;
      this.attributes = attributes;
    }

    public final Map<String, Object> getAttributes() {
      return this.attributes;
    }

    public final Principal getPrincipal() {
      return this.principal;
    }

    public final boolean equals(Object o) {
      if (o == null)
        return false; 
      if (!(o instanceof org.jasig.cas.authentication.AbstractAuthentication))
        return false; 
      return true;
    }

    public final int hashCode() {
      return 49 * this.principal.hashCode() ^ getAuthenticatedDate().hashCode();
    }

    public final String toString() {
      return "[Principal=" + this.principal.getId() + ", attributes=" + this.attributes.toString() + "]";
    }
}
