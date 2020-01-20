/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.support;

import com.passport.Configuration;
import com.passport.ModifyHeaderUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 *
 * @author TrungBH
 */
public class CookieRetrievingCookieGenerator extends CookieGenerator{
    private int rememberMeMaxAge = 7889231;

    public void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieValue) {
      String ip = "";
      String mac = "";
      String wan = "";
      if (Configuration.getInstance().isBalancerUsed()) {
        wan = request.getHeader("X-Forwarded-For");
      } else {
        wan = request.getRemoteAddr();
      } 
      ip = request.getHeader("VTS-IP");
      if (ip != null && ip.trim().length() > 0) {
        try {
          ip = ModifyHeaderUtils.parseIP(ip);
        } catch (Exception e) {
          this.logger.info("Exception when parse ip " + ip + " :" + e, e);
          ip = "";
        } 
      } else {
        ip = "";
      } 
      mac = request.getHeader("VTS-MAC");
      if (mac != null && mac.trim().length() > 0) {
        try {
          mac = ModifyHeaderUtils.parseMAC(mac);
        } catch (Exception e) {
          this.logger.info("Exception when parse mac " + mac + " :" + e, e);
          mac = "";
        } 
      } else {
        mac = "";
      } 
      String cookiesWithUserInfo = cookieValue + "#" + ip + "#" + mac + "#" + wan;
      String encrytedCookies = "";
      try {
        encrytedCookies = ModifyHeaderUtils.encodeBase64(cookiesWithUserInfo.getBytes());
      } catch (Exception ex) {
        this.logger.error(ex);
      } 
      this.logger.info("|||||| add cookies " + encrytedCookies);
      if (!StringUtils.hasText(request.getParameter("rememberMe"))) {
        addCookie(response, encrytedCookies);
      } else {
          Cookie cookie = createCookie(encrytedCookies);
        cookie.setMaxAge(this.rememberMeMaxAge);
        if (isCookieSecure())
          cookie.setSecure(true); 
        response.addCookie(cookie);
      } 
    }

    public String retrieveCookieValue(HttpServletRequest request) {
      Cookie cookie = WebUtils.getCookie(request, getCookieName());
      String ip = "";
      String mac = "";
      String wan = "";
      if (Configuration.getInstance().isBalancerUsed()) {
        wan = request.getHeader("X-Forwarded-For");
      } else {
        wan = request.getRemoteAddr();
      } 
      ip = request.getHeader("VTS-IP");
      if (ip != null && ip.trim().length() > 0) {
        try {
          ip = ModifyHeaderUtils.parseIP(ip);
        } catch (Exception e) {
          this.logger.info("Exception when parse ip " + ip + " :" + e, e);
          ip = "";
        } 
      } else {
        ip = "";
      } 
      mac = request.getHeader("VTS-MAC");
      if (mac != null && mac.trim().length() > 0) {
        try {
          mac = ModifyHeaderUtils.parseMAC(mac);
        } catch (Exception e) {
          this.logger.info("Exception when parse mac " + mac + " :" + e, e);
          mac = "";
        } 
      } else {
        mac = "";
      } 
      if (cookie != null) {
        String ipInCookies, macInCookies, wanInCookies, cookieFromUser = "";
        try {
          cookieFromUser = new String(ModifyHeaderUtils.decodeBase64(cookie.getValue()));
        } catch (Exception e) {
          this.logger.error(e);
          return null;
        } 
        String[] info = cookieFromUser.split("#");
        try {
          ipInCookies = info[1];
          macInCookies = info[2];
          wanInCookies = info[3];
        } catch (Exception e) {
          this.logger.error(e);
          return null;
        } 
        if (ip.equalsIgnoreCase(ipInCookies) && mac.equalsIgnoreCase(macInCookies) && wan.equalsIgnoreCase(wanInCookies))
          return cookieFromUser; 
        this.logger.error(String.format("User with IPLAN %s , Mac %s , IPWAN %s stolen cookies of User with IPLAN %s , Mac %s , IPWAN %s", new Object[] { ip, mac, wan, ipInCookies, macInCookies, wanInCookies }));
      } 
      return null;
    }

    public void setRememberMeMaxAge(int maxAge) {
      this.rememberMeMaxAge = maxAge;
    }
}
