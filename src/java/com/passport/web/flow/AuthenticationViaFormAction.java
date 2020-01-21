/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web.flow;

import com.passport.Configuration;
import com.passport.DbManager;
import com.passport.ModifyHeaderUtils;
import com.passport.Users;
import com.passport.validation.UsernamePasswordCredentialsValidator;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import md5.PasswordService;
import org.inspektr.common.ioc.annotation.NotNull;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handle.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.bind.CredentialsBinder;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 *
 * @author TrungBH
 */
public class AuthenticationViaFormAction extends FormAction{
    private CredentialsBinder credentialsBinder;

    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieGenerator warnCookieGenerator;

    protected final void doBind(RequestContext context, DataBinder binder) throws Exception {
      HttpServletRequest request = WebUtils.getHttpServletRequest(context);
      Credentials credentials = (Credentials)binder.getTarget();
      if (this.credentialsBinder != null)
        this.credentialsBinder.bind(request, credentials); 
      super.doBind(context, binder);
    }

    public Event referenceData(RequestContext context) throws Exception {
      context.getRequestScope().put("commandName", getFormObjectName());
      HttpServletRequest request = WebUtils.getHttpServletRequest(context);
      Long loginCount = Long.valueOf(0L);
      if (request.getSession().getAttribute("loginCount") != null)
        try {
          loginCount = (Long)request.getSession().getAttribute("loginCount");
        } catch (Exception en) {
          this.logger.error(en);
        }  
      context.getRequestScope().put("loginCount", loginCount);
      if (Configuration.getInstance().isCaptchaUsed())
        context.getRequestScope().put("isCaptchaUsed", "true"); 
      return success();
    }

    public final Event submit(RequestContext context) throws Exception {
      Credentials credentials = (Credentials)getFormObject(context);
      String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
      WebApplicationService webApplicationService = WebUtils.getService(context);
      if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null && webApplicationService != null)
        try {
          String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, (Service)webApplicationService, credentials);
          WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
          putWarnCookieIfRequestParameterPresent(context);
          return warn();
        } catch (TicketException e) {
          if (e.getCause() != null && AuthenticationException.class.isAssignableFrom(e.getCause().getClass())) {
            populateErrorsInstance(context, e);
            return error();
          } 
          this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
          if (this.logger.isDebugEnabled())
            this.logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", (Throwable)e); 
        }  
      try {
        WebUtils.putTicketGrantingTicketInRequestScope(context, this.centralAuthenticationService.createTicketGrantingTicket(credentials));
        putWarnCookieIfRequestParameterPresent(context);
        if (webApplicationService == null)
          return success(); 
        if (!Configuration.getInstance().isWhiteList())
          return success(); 
        DbManager db = new DbManager();
        try {
          List<String> trustList = db.getWhiteListIp();
          for (int i = 0; i < trustList.size(); i++) {
            String ip = trustList.get(i);
            String redirectUrl = webApplicationService.toString();
            if (redirectUrl.startsWith(ip))
              return success(); 
          } 
        } catch (Exception ex) {
          this.logger.error("Error when getting white list ip ", ex);
          Errors errors1 = getFormErrors(context);
          errors1.reject("msg.error.not_in_white_list", "Error when getting white list ip");
          return result("error");
        } finally {
          db.close();
        } 
        Errors errors = getFormErrors(context);
        errors.reject("msg.error.not_in_white_list", "Redirect url not in white list !");
        return result("error");
      } catch (TicketException e) {
        populateErrorsInstance(context, e);
        return error();
      } 
    }

    private final Event warn() {
      return result("warn");
    }

    public final Event authen(RequestContext context) throws Exception {
      long start = System.currentTimeMillis();
      long elapse = 0L;
      this.logger.info("Start authenticate user.");
      bindAndValidate(context);
      Errors errors = getFormErrors(context);
      HttpServletRequest request = WebUtils.getHttpServletRequest(context);
      String ip = "";
      String mac = "";
      String wan = "";
      String headerVersion = "";
      if (Configuration.getInstance().isBalancerUsed()) {
        wan = request.getHeader("X-Forwarded-For");
      } else {
        wan = request.getRemoteAddr();
      } 
      if (Configuration.getInstance().isIpFilter()) {
        if (wan == null || wan.trim().length() == 0) {
          elapse = System.currentTimeMillis() - start;
          this.logger.info("Authenticate failure, elapse time = " + elapse);
          if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
          errors.reject("msg.error.modifyHeader.required.wan", "Không tìm thấy địa chỉ IP trong request header, yêu cầu không thể tiếp tục");
          return result("error");
        } 
        ip = request.getHeader("VTS-IP");
        mac = request.getHeader("VTS-MAC");
        headerVersion = request.getHeader("VTS-VER");
        if (headerVersion != null && headerVersion.trim().length() > 0)
          try {
            headerVersion = ModifyHeaderUtils.parseVersion(headerVersion.trim());
          } catch (Exception e) {
            this.logger.info("Exception when parse header version " + headerVersion.trim() + " :" + e, e);
            headerVersion = "";
          }  
        String headerVersions = Configuration.getInstance().getModifyHeaderVersions();
        if (headerVersion == null || headerVersion.trim().length() == 0 || !ModifyHeaderUtils.isValidVersion(headerVersion, headerVersions)) {
          elapse = System.currentTimeMillis() - start;
          this.logger.info("Authenticate failure, elapse time = " + elapse);
          if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
          errors.reject("msg.error.modifyHeader.invalid.version", "Phiên bản modifyHeader bạn đang dùng không đúng, hãy tải bản mới nhất về để cài đặt trên máy");
          return result("error");
        } 
        if (ip != null && ip.trim().length() > 0)
          try {
            ip = ModifyHeaderUtils.parseIP(ip);
          } catch (Exception e) {
            this.logger.info("Exception when parse ip " + ip + " :" + e, e);
            ip = "";
          }  
        if (ip == null || ip.trim().length() == 0) {
          elapse = System.currentTimeMillis() - start;
          this.logger.info("Authenticate failure, elapse time = " + elapse);
          if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
          ip = "";
          errors.reject("msg.error.modifyHeader.required.ip", "Không tìm thấy địa chỉ IP LAN trong request header, yêu cầu không thể tiếp tục");
          errors.reject("msg.error.modifyHeader.support", "Để khắc phục lỗi này bạn cần cài thêm plugin ModifyHeader cho Firefox");
          return result("error");
        } 
        if (mac != null && mac.trim().length() > 0)
          try {
            mac = ModifyHeaderUtils.parseMAC(mac);
          } catch (Exception e) {
            this.logger.info("Exception when parse mac " + mac + " :" + e, e);
            mac = "";
          }  
      } 
      UsernamePasswordCredentials c = (UsernamePasswordCredentials)getFormObject(context);
      String userName = c.getUsername().trim().toLowerCase();
      String password = c.getPassword().trim();
      c.setUsername(userName);
      Long loginCount = Long.valueOf(0L);
      if (request.getSession().getAttribute("loginCount") != null)
        try {
          loginCount = (Long)request.getSession().getAttribute("loginCount");
        } catch (Exception en) {
          this.logger.error(en);
          en.printStackTrace();
        }  
      loginCount = Long.valueOf(loginCount.longValue() + 1L);
      request.getSession().setAttribute("loginCount", loginCount);
      context.getRequestScope().put("loginCount", loginCount);
      if (Configuration.getInstance().isCaptchaUsed() && 
        loginCount.longValue() >= 6L) {
        String captcha = c.getCaptcha().trim();
        String sessionCaptcha = (String)request.getSession().getAttribute("captcha_key_name");
        if (sessionCaptcha == null || !sessionCaptcha.equals(captcha)) {
          errors.reject("msg.error.captcha.incorrect", "Mã ký tự bảo vệ không đúng");
          return result("error");
        } 
      } 
      String domainCode = context.getRequestParameters().get("appCode");
      if (userName.length() == 0) {
        elapse = System.currentTimeMillis() - start;
        this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
        if (elapse > 60000L)
          this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
        errors.reject("required.username", "Bạn chưa nhập tên đăng nhập");
        return result("error");
      } 
      if (password.length() == 0) {
        elapse = System.currentTimeMillis() - start;
        this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
        if (elapse > 60000L)
          this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
        errors.reject("required.password", "Bạn chưa nhập mật khẩu");
        return result("error");
      } 
      DbManager db = new DbManager();
      try {
        db.setIp(ip);
        db.setWan(wan);
        db.setMac(mac);
        if (domainCode != null && domainCode.length() != 0) {
          long appId = db.getAppID(domainCode).longValue();
          if (appId == -1L) {
            elapse = System.currentTimeMillis() - start;
            this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
            if (elapse > 60000L)
              this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
            errors.reject("msg.error.app.inactive", "Ứng dụng chưa được kích hoạt, hoặc đang tạm khóa");
            errors.reject("error", db.getAppLockMessage(domainCode));
            return result("error");
          } 
        } 
        int authen = db.authenDomain(domainCode, userName);
        if (authen == 0) {
          elapse = System.currentTimeMillis() - start;
          this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
          if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
          errors.reject("msg.error.permission.deny", "Tài khoản hoặc mật khẩu không đúng");
          return result("error");
        } 
        if (authen == -1) {
          elapse = System.currentTimeMillis() - start;
          this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
          if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
          errors.reject("msg.error.database.error", "Lỗi khi kết nối đến database!");
          return result("error");
        } 
        Users ubo = db.getUser(userName);
        String ecryptPass = null;
        PasswordService pwdSrv = PasswordService.getInstance();
        try {
          if (ubo.getPasswordChanged() != null && ubo.getPasswordChanged().equals(Long.valueOf(1L))) {
            ecryptPass = pwdSrv.encrypt(userName + password);
          } else if (ubo.getPasswordChanged() != null && ubo.getPasswordChanged().equals(Long.valueOf(2L))) {
            ecryptPass = PasswordService.hashPassword(userName + password);
          } else {
            ecryptPass = pwdSrv.encrypt(password);
          } 
        } catch (Exception ex) {
          this.logger.error(ex, ex);
        } 
        if (!ubo.getPassword().replaceAll("\n", "").replaceAll("\r", "").equalsIgnoreCase(ecryptPass.replaceAll("\r", "").replaceAll("\n", ""))) {
          errors.reject("msg.error.password.incorrect", "Tài khoản hoặc mật khẩu không đúng");
          return result("error");
        } 
        db.logLogin(userName);
      } catch (Exception ex) {
        this.logger.error("Authenticate user [" + userName + "]error.", ex);
        elapse = System.currentTimeMillis() - start;
        this.logger.info("Authenticate failure for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
        if (elapse > 60000L)
          this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
        errors.reject("msg.error.authenticate", "Có lỗi xảy ra khi xác thực người dùng");
        return result("error");
      } finally {
        try {
          db.close();
        } catch (Exception e) {
          this.logger.error("Close database connection error.", e);
        } 
      } 
      elapse = System.currentTimeMillis() - start;
      this.logger.info("Authenticate success for user [" + userName + "], domain code [" + domainCode + "], elapse time = " + elapse);
      if (elapse > 60000L)
        this.logger.error("Action timeout: Too long time to authenticate user, elapse time = " + elapse); 
      return success();
    }

    private final void populateErrorsInstance(RequestContext context, TicketException e) {
      try {
        Errors errors = getFormErrors(context);
        errors.reject(e.getCode(), e.getCode());
      } catch (Exception fe) {
        this.logger.error(fe, fe);
      } 
    }

    private void putWarnCookieIfRequestParameterPresent(RequestContext context) {
      HttpServletResponse response = WebUtils.getHttpServletResponse(context);
      if (StringUtils.hasText(context.getExternalContext().getRequestParameterMap().get("warn"))) {
        this.warnCookieGenerator.addCookie(response, "true");
      } else {
        this.warnCookieGenerator.removeCookie(response);
      } 
    }

    public final void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
      this.centralAuthenticationService = centralAuthenticationService;
    }

    public final void setCredentialsBinder(CredentialsBinder credentialsBinder) {
      this.credentialsBinder = credentialsBinder;
    }

    public final void setWarnCookieGenerator(CookieGenerator warnCookieGenerator) {
      this.warnCookieGenerator = warnCookieGenerator;
    }

    protected void initAction() {
      if (getFormObjectClass() == null) {
        setFormObjectClass(UsernamePasswordCredentials.class);
        setFormObjectName("credentials");
        setValidator((Validator)new UsernamePasswordCredentialsValidator());
        this.logger.info("FormObjectClass not set.  Using default class of " + getFormObjectClass().getName() + " with formObjectName " + getFormObjectName() + " and validator " + getValidator().getClass().getName() + ".");
      } 
      Assert.isTrue(Credentials.class.isAssignableFrom(getFormObjectClass()), "CommandClass must be of type Credentials.");
      if (this.credentialsBinder != null)
        Assert.isTrue(this.credentialsBinder.supports(getFormObjectClass()), "CredentialsBinder does not support supplied FormObjectClass: " + getClass().getName()); 
    }

    public static void main(String[] args) {
      String regex = "\\b(?:\\d{1,3}.){3}\\d{1,3}\\b|192.168.176.191";
      regex = regex.replace(".", "\\.");
      RegularExpression re = new RegularExpression(regex);
      if (re.matches("192.168.176.190")) {
        System.out.println("Okie");
      } else {
        System.out.println("Not Okie");
      } 
    }
}
