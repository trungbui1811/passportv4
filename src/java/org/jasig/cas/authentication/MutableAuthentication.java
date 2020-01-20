/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication;

import java.util.Date;
import java.util.HashMap;
import org.jasig.cas.authentication.principal.Principal;

/**
 *
 * @author TrungBH
 */
public class MutableAuthentication extends AbstractAuthentication{
    private static final long serialVersionUID = -4415875344376642246L;

    private final Date authenticatedDate;

    public MutableAuthentication(Principal principal) {
      this(principal, new Date());
    }

    public MutableAuthentication(Principal principal, Date date) {
      super(principal, new HashMap<String, Object>());
      this.authenticatedDate = date;
    }

    public Date getAuthenticatedDate() {
      return this.authenticatedDate;
    }
}
