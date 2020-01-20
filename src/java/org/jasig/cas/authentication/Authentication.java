/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.jasig.cas.authentication.principal.Principal;

/**
 *
 * @author TrungBH
 */
public interface Authentication extends Serializable{
    Principal getPrincipal();
    Date getAuthenticatedDate();
    Map<String, Object> getAttributes();
}
