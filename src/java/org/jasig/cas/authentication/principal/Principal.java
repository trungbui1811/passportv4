/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author TrungBH
 */
public interface Principal extends Serializable{
    String getId();
    Map<String, Object> getAttributes();
}
