/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.support;

import javax.servlet.http.HttpServletRequest;
import org.jasig.cas.authentication.principal.WebApplicationService;

/**
 *
 * @author TrungBH
 */
public interface ArgumentExtractor {
    WebApplicationService extractService(HttpServletRequest paramHttpServletRequest);
}
