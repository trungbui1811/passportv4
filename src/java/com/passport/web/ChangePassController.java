/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web;

import com.passport.Configuration;
import com.passport.DbManager;
import com.passport.ModifyHeaderUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 *
 * @author TrungBH
 */
public class ChangePassController extends SimpleFormController{
    private final Log logger = LogFactory.getLog(ChangePassController.class);
    
    public ChangePassController() {
        setCommandClass(ChangePass.class);
        setCommandName("changePass");
        setSuccessView("casChangePasswordSuccessView");
        setFormView("casChangePasswordView");
        setValidator((Validator)new ChangepPassValidator());
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        ChangePass changePass = (ChangePass)command;
        String ip = "";
        String wan = "";
        String mac = "";
        String headerVersion = null;
        if (Configuration.getInstance().isBalancerUsed()) {
            wan = request.getHeader("X-Forwarded-For");
        } else {
            wan = request.getRemoteAddr();
        } 
        if (Configuration.getInstance().isIpFilter()) {
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
                errors.reject("msg.error.modifyHeader.invalid.version", "Phiên bản modifyHeader bạn đang dùng không đúng, hãy tải bản mới nhất về để cài đặt trên máy.");
                ModelAndView modelAndView = new ModelAndView(getFormView(), errors.getModel());
                return modelAndView;
            } 
            ip = request.getHeader("VTS-IP");
            if (ip != null && ip.length() > 0) {
                ip = ModifyHeaderUtils.parseIP(ip);
            } else {
                ip = "";
                errors.reject("msg.error.modifyHeader.required.ip", "Không tìm thấy địa chỉ IP trong request header, yêu cầu không thể tiếp tục");
                errors.reject("msg.error.modifyHeader.support", "Để khắc phục lỗi này bạn cần cài thêm plugin ModifyHeader cho Firefox(Download rồi kéo thả vào trình duyệt firefox)");
                ModelAndView modelAndView = new ModelAndView(getFormView(), errors.getModel());
                return modelAndView;
            } 
            mac = request.getHeader("VTS-MAC");
            if (mac != null && mac.trim().length() > 0)
              try {
                  mac = ModifyHeaderUtils.parseMAC(ip);
              } catch (Exception e) {
                  this.logger.info("Exception when parse mac " + mac + " :" + e, e);
                  mac = "";
              }  
        } 
        DbManager db = new DbManager();
        int chgPwdOk = 0;
        try {
            db.setIp(ip);
            db.setWan(wan);
            db.setMac(mac);
            chgPwdOk = db.changePassword(changePass.getUserName(), changePass.getOldPass(), changePass.getNewPass());
        } catch (Exception e) {
            this.logger.error(e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        if (chgPwdOk == 1) {
            this.logger.info("Changed password successfull for user " + changePass.getUserName());
            ModelAndView modelAndView = new ModelAndView(getSuccessView());
            return modelAndView;
        } 
        this.logger.info("Error when changed password for user " + changePass.getUserName());
        Map<String, Object> modelAux = new HashMap<>();
        modelAux.put("changePass", changePass);
        ModelAndView mv = new ModelAndView(getFormView(), modelAux);
        return mv;
    }
}
