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
public class Cas20ProtocolValidationSpecification extends AbstractCasProtocolValidationSpecification{
    public Cas20ProtocolValidationSpecification() {}

    public Cas20ProtocolValidationSpecification(boolean renew) {
      super(renew);
    }

    protected boolean isSatisfiedByInternal(Assertion assertion) {
      return true;
    }
}
