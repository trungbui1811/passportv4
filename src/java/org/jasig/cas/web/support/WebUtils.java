/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.support;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.springframework.util.Assert;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;

/**
 *
 * @author TrungBH
 */
public final class WebUtils {
    public static final HttpServletRequest getHttpServletRequest(RequestContext context) {
      Assert.isInstanceOf(ServletExternalContext.class, context.getExternalContext(), "Cannot obtain HttpServletRequest from event of type: " + context.getExternalContext().getClass().getName());
      return ((ServletExternalContext)context.getExternalContext()).getRequest();
    }

    public static final HttpServletResponse getHttpServletResponse(RequestContext context) {
      Assert.isInstanceOf(ServletExternalContext.class, context.getExternalContext(), "Cannot obtain HttpServletResponse from event of type: " + context.getExternalContext().getClass().getName());
      return ((ServletExternalContext)context.getExternalContext()).getResponse();
    }

    public static final WebApplicationService getService(List<ArgumentExtractor> argumentExtractors, HttpServletRequest request) {
      for (ArgumentExtractor argumentExtractor : argumentExtractors) {
        WebApplicationService service = argumentExtractor.extractService(request);
        if (service != null)
          return service; 
      } 
      return null;
    }

    public static final WebApplicationService getService(List<ArgumentExtractor> argumentExtractors, RequestContext context) {
      HttpServletRequest request = getHttpServletRequest(context);
      return getService(argumentExtractors, request);
    }

    public static final WebApplicationService getService(RequestContext context) {
      return (WebApplicationService)context.getFlowScope().get("service");
    }

    public static final void putTicketGrantingTicketInRequestScope(RequestContext context, String ticketValue) {
      context.getRequestScope().put("ticketGrantingTicketId", ticketValue);
    }

    public static final String getTicketGrantingTicketId(RequestContext context) {
      String tgtFromRequest = (String)context.getRequestScope().get("ticketGrantingTicketId");
      String tgtFromFlow = (String)context.getFlowScope().get("ticketGrantingTicketId");
      return (tgtFromRequest != null) ? tgtFromRequest : tgtFromFlow;
    }

    public static final void putServiceTicketInRequestScope(RequestContext context, String ticketValue) {
      context.getRequestScope().put("serviceTicketId", ticketValue);
    }

    public static final String getServiceTicketFromRequestScope(RequestContext context) {
      return context.getRequestScope().getString("serviceTicketId");
    }
}
