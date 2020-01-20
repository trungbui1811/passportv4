/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.validation;

/**
 *
 * @author TrungBH
 */
public abstract class AbstractCasProtocolValidationSpecification implements ValidationSpecification{
    private boolean renew;

    public AbstractCasProtocolValidationSpecification() {
      this.renew = false;
    }

    public AbstractCasProtocolValidationSpecification(boolean renew) {
      this.renew = renew;
    }

    public final void setRenew(boolean renew) {
      this.renew = renew;
    }

    public final boolean isRenew() {
      return this.renew;
    }

    public final boolean isSatisfiedBy(Assertion assertion) {
      return (isSatisfiedByInternal(assertion) && (!this.renew || (assertion.isFromNewLogin() && this.renew)));
    }

    protected abstract boolean isSatisfiedByInternal(Assertion paramAssertion);
}
