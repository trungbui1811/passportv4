/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.validation;

import java.io.Serializable;
import java.util.List;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public interface Assertion extends Serializable{
    List<Authentication> getChainedAuthentications();
    boolean isFromNewLogin();
    Service getService();
}
