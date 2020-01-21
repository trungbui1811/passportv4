/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web.view;

import com.passport.JDBCUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jasig.cas.web.view.AbstractCasView;
import org.w3c.dom.Document;

/**
 *
 * @author TrungBH
 */
public final class Cas10ResponseView extends AbstractCasView{
    private boolean successResponse;

    protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      response.setContentType("text/html; charset=UTF-8");
      if (this.successResponse) {
        String domainCode = (String)model.get("domainCode");
        boolean authenDomain = ((Boolean)model.get("authenDomain")).booleanValue();
        if (authenDomain) {
          Document doc = (Document)model.get("XML");
          response.getWriter().print(JDBCUtil.serialize(doc));
        } else {
          response.getWriter().print("no," + domainCode);
        } 
      } else {
        response.getWriter().print("no");
      } 
    }

    public void setSuccessResponse(boolean successResponse) {
      this.successResponse = successResponse;
    }
}
