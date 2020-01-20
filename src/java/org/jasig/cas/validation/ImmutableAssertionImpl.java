/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.validation;

import java.util.Collections;
import java.util.List;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
public class ImmutableAssertionImpl implements Assertion{
    private static final long serialVersionUID = -1921502350732798866L;

    private final List<Authentication> principals;

    private final boolean fromNewLogin;

    private final Service service;

    public ImmutableAssertionImpl(List<Authentication> principals, Service service, boolean fromNewLogin) {
      Assert.notNull(principals, "principals cannot be null");
      Assert.notNull(service, "service cannot be null");
      Assert.notEmpty(principals, "principals cannot be empty");
      this.principals = principals;
      this.service = service;
      this.fromNewLogin = fromNewLogin;
    }

    public List<Authentication> getChainedAuthentications() {
      return Collections.unmodifiableList(this.principals);
    }

    public boolean isFromNewLogin() {
      return this.fromNewLogin;
    }

    public Service getService() {
      return this.service;
    }

    public boolean equals(Object o) {
      return true;
    }

    public int hashCode() {
      return 15 * this.service.hashCode() ^ this.principals.hashCode();
    }

    public String toString() {
      return "[principals={" + this.principals.toString() + "} for service=" + this.service.toString() + "]";
    }
}
