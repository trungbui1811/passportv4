/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

/**
 *
 * @author TrungBH
 */
public interface WebApplicationService extends Service{
    Response getResponse(String paramString);
    String getArtifactId();
}
