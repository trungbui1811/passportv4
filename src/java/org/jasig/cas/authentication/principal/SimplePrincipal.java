/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
public class SimplePrincipal implements Principal{
    private static final Map<String, Object> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Object>());

    private static final long serialVersionUID = -5265620187476296219L;

    private final String id;

    private Map<String, Object> attributes;

    public SimplePrincipal(String id) {
      this(id, null);
    }

    public SimplePrincipal(String id, Map<String, Object> attributes) {
      Assert.notNull(id, "id cannot be null");
      this.id = id;
      this.attributes = (attributes == null || attributes.isEmpty()) ? EMPTY_MAP : Collections.<String, Object>unmodifiableMap(attributes);
    }

    public Map<String, Object> getAttributes() {
      return this.attributes;
    }

    public String toString() {
      return this.id;
    }

    public int hashCode() {
      return super.hashCode() ^ this.id.hashCode();
    }

    public final String getId() {
      return this.id;
    }

    public boolean equals(Object o) {
      if (o == null || !getClass().equals(o.getClass()))
        return false; 
      SimplePrincipal p = (SimplePrincipal)o;
      return this.id.equals(p.getId());
    }
}
