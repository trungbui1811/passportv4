/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web;

import com.passport.DbManager;
import com.passport.JDBCUtil;
import java.net.URL;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.inspektr.common.ioc.annotation.NotNull;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.HttpBasedServiceCredentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketValidationException;
import org.jasig.cas.ticket.proxy.ProxyHandler;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.validation.Cas20ProtocolValidationSpecification;
import org.jasig.cas.validation.ValidationSpecification;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;

/**
 *
 * @author TrungBH
 */
public class ServiceValidateController extends AbstractController{
    private static final String DEFAULT_SERVICE_FAILURE_VIEW_NAME = "casServiceFailureView";

    private static final String DEFAULT_SERVICE_SUCCESS_VIEW_NAME = "casServiceSuccessView";

    private static final String MODEL_PROXY_GRANTING_TICKET_IOU = "pgtIou";

    private static final String MODEL_ASSERTION = "assertion";

    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private Class<?> validationSpecificationClass = Cas20ProtocolValidationSpecification.class;

    @NotNull
    private ProxyHandler proxyHandler;

    @NotNull
    private String successView = "casServiceSuccessView";

    @NotNull
    private String failureView = "casServiceFailureView";

    @NotNull
    private ArgumentExtractor argumentExtractor;

    protected Credentials getServiceCredentialsFromRequest(HttpServletRequest request) {
      String pgtUrl = request.getParameter("pgtUrl");
      if (StringUtils.hasText(pgtUrl))
        try {
          return (Credentials)new HttpBasedServiceCredentials(new URL(pgtUrl));
        } catch (Exception e) {
          this.logger.error("Error constructing pgtUrl", e);
        }  
      return null;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      binder.setRequiredFields(new String[] { "renew" });
    }

    protected final ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
      long start = System.currentTimeMillis();
      WebApplicationService service = this.argumentExtractor.extractService(request);
      String serviceTicketId = (service != null) ? service.getArtifactId() : null;
      if (service == null || serviceTicketId == null) {
        this.logger.info("Invalidate request because service of service ticket id is null.");
        return generateErrorView("INVALID_REQUEST", "INVALID_REQUEST", null);
      } 
      this.logger.info("Start validate for ticket[" + serviceTicketId + "], service [" + service + "].");
      try {
        Credentials serviceCredentials = getServiceCredentialsFromRequest(request);
        String proxyGrantingTicketId = null;
        if (serviceCredentials != null)
          try {
            proxyGrantingTicketId = this.centralAuthenticationService.delegateTicketGrantingTicket(serviceTicketId, serviceCredentials);
          } catch (TicketException e) {
            this.logger.error("TicketException generating ticket for: " + serviceCredentials, (Throwable)e);
          }  
        Assertion assertion = this.centralAuthenticationService.validateServiceTicket(serviceTicketId, (Service)service);
        ValidationSpecification validationSpecification = getCommandClass();
        ServletRequestDataBinder binder = new ServletRequestDataBinder(validationSpecification, "validationSpecification");
        initBinder(request, binder);
        binder.bind((ServletRequest)request);
        if (!validationSpecification.isSatisfiedBy(assertion)) {
          this.logger.info("ServiceTicket [" + serviceTicketId + "] does not satisfy validation specification.");
          return generateErrorView("INVALID_TICKET", "INVALID_TICKET_SPEC", null);
        } 
        String domainCode = request.getParameter("domainCode");
        String userName = ((Authentication)assertion.getChainedAuthentications().get(0)).getPrincipal().getId();
        this.logger.info("User for validate:" + userName);
        boolean authenDomain = true;
        DbManager db = new DbManager();
        Document doc = JDBCUtil.createDocument();
        long startQuery = System.currentTimeMillis();
        try {
          doc = db.getUserData(userName, doc);
          doc = db.getRolesData(userName, doc);
          doc = db.getListObjects(userName, domainCode, doc);
        } catch (Exception e) {
          this.logger.error("Get user data error" + e.getMessage(), e);
        } finally {
          try {
            db.close();
          } catch (Exception e) {
            this.logger.error("Close database connection error.", e);
          } 
        } 
        long queryTime = System.currentTimeMillis() - startQuery;
        ModelAndView success = new ModelAndView(this.successView);
        success.addObject("assertion", assertion);
        success.addObject("authenDomain", Boolean.valueOf(authenDomain));
        success.addObject("domainCode", domainCode);
        success.addObject("XML", doc);
        if (serviceCredentials != null && proxyGrantingTicketId != null) {
          String proxyIou = this.proxyHandler.handle(serviceCredentials, proxyGrantingTicketId);
          success.addObject("pgtIou", proxyIou);
        } 
        long elapse = System.currentTimeMillis() - start;
        this.logger.info("Finish validate for user[" + userName + "] with domain code [" + domainCode + "], sevice ticket [" + serviceTicketId + "], query time = " + queryTime + ", validate time = " + elapse);
        if (elapse > 60000L)
          this.logger.error("Action timeout: Too long time to validate user, elapse time = " + elapse); 
        return success;
      } catch (TicketValidationException e) {
        this.logger.error("Exception validate service ticket [" + serviceTicketId + "].", (Throwable)e);
        return generateErrorView(e.getCode(), e.getCode(), new Object[] { serviceTicketId, e.getOriginalService().getId(), service.getId() });
      } catch (TicketException te) {
        this.logger.error("Exception validate service ticket [" + serviceTicketId + "].", (Throwable)te);
        return generateErrorView(te.getCode(), te.getCode(), new Object[] { serviceTicketId });
      } catch (UnauthorizedServiceException e) {
        this.logger.error("Exception validate service ticket [" + serviceTicketId + "].", (Throwable)e);
        return generateErrorView(e.getMessage(), e.getMessage(), null);
      } 
    }

    private ModelAndView generateErrorView(String code, String description, Object[] args) {
      ModelAndView modelAndView = new ModelAndView(this.failureView);
      modelAndView.addObject("code", code);
      modelAndView.addObject("description", getMessageSourceAccessor().getMessage(description, args, description));
      return modelAndView;
    }

    private ValidationSpecification getCommandClass() {
      try {
        return (ValidationSpecification)this.validationSpecificationClass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      } 
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
      this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setArgumentExtractor(ArgumentExtractor argumentExtractor) {
      this.argumentExtractor = argumentExtractor;
    }

    public void setValidationSpecificationClass(Class<?> validationSpecificationClass) {
      this.validationSpecificationClass = validationSpecificationClass;
    }

    public void setFailureView(String failureView) {
      this.failureView = failureView;
    }

    public void setSuccessView(String successView) {
      this.successView = successView;
    }

    public void setProxyHandler(ProxyHandler proxyHandler) {
      this.proxyHandler = proxyHandler;
    }
}
