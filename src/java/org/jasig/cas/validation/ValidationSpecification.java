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
public interface ValidationSpecification {
    boolean isSatisfiedBy(Assertion paramAssertion);
}
