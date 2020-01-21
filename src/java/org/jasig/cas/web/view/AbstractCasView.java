/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.view;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.validation.Assertion;
import org.springframework.web.servlet.view.AbstractView;

/**
 *
 * @author TrungBH
 */
public abstract class AbstractCasView extends AbstractView{
    protected final Log log = LogFactory.getLog(getClass());

    protected final Assertion getAssertionFrom(Map model) {
      return (Assertion)model.get("assertion");
    }
}
