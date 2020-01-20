/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web;

import com.passport.DbManager;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.inspektr.common.ioc.annotation.NotNull;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author TrungBH
 */
public final class LogoutController extends AbstractController{
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @NotNull
    private CookieRetrievingCookieGenerator warnCookieGenerator;

    @NotNull
    private String logoutView;
    
    private boolean followServiceRedirects;

    public LogoutController() {
        setCacheSeconds(0);
    }

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ticketGrantingTicketId = this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        String service = request.getParameter("service");
        DbManager db = new DbManager();
        List<String> trustList = db.getWhiteListIp();
        db.close();
        for (int i = 0; i < trustList.size(); i++) {
            String ip = trustList.get(i);
            String redirectUrl = service.toString();
            if (redirectUrl.startsWith(ip)) {
                if (ticketGrantingTicketId != null) {
                    this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
                    this.ticketGrantingTicketCookieGenerator.removeCookie(response);
                    this.warnCookieGenerator.removeCookie(response);
                } 
                if (service != null)
                    return new ModelAndView((View)new RedirectView(service)); 
                request.getSession().invalidate();
                return new ModelAndView(this.logoutView);
            } 
        } 
        return null;
    }

    public void setTicketGrantingTicketCookieGenerator(CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
    }

    public void setWarnCookieGenerator(CookieRetrievingCookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setFollowServiceRedirects(boolean followServiceRedirects) {
        this.followServiceRedirects = followServiceRedirects;
    }

    public void setLogoutView(String logoutView) {
        this.logoutView = logoutView;
    }
}
