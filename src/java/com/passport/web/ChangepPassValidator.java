/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web;

import com.passport.AuthenticationErrorCode;
import com.passport.Configuration;
import com.passport.DbManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author TrungBH
 */
public class ChangepPassValidator implements Validator{
    private final Log logger = LogFactory.getLog(getClass());

    public boolean supports(Class arg0) {
        return ChangePass.class.equals(arg0);
    }

    public void validate(Object obj, Errors errors) {
        ChangePass changePass = (ChangePass)obj;
        AuthenticationErrorCode errorCode = AuthenticationErrorCode.DATABASE_ERROR;
        DbManager db = new DbManager();
        try {
            errorCode = db.checkAccount(changePass.getUserName(), changePass.getOldPass(), true);
        } catch (Exception e) {
            this.logger.error("Check account error.", e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        if (changePass.getUserName().trim().length() == 0) {
          errors.reject("userName", "Bạn chưa nhập tên đăng nhập");
        } else if (changePass.getOldPass().trim().length() == 0) {
          errors.reject("oldPass", "Bạn chưa nập mật khẩu cũ");
        } else if (changePass.getNewPass().trim().length() == 0) {
          errors.reject("oldPass", "Bạn chưa nhập mật khẩu mới");
        } else if (changePass.getRetypedPass().trim().length() == 0) {
          errors.reject("oldPass", "Bạn chưa nhập mật khẩu xác nhận");
        } else if (!changePass.getNewPass().equals(changePass.getRetypedPass())) {
          errors.reject("oldPass", "Mật khẩu xác nhận không đúng");
        } else if (!validateStrongPassword(changePass.getNewPass())) {
          errors.reject("oldPass", "Mật khẩu không đúng định dạng");
        } else if (changePass.getNewPass().equals(changePass.getOldPass())) {
          errors.reject("oldPass", "Mật khẩu mới không được trùng với mật khẩu cũ");
        } else if (errorCode == AuthenticationErrorCode.USER_NOT_EXIST) {
          errors.reject("oldPass", "Tài khoản chưa tồn tại trên hệ thống");
        } else if (errorCode == AuthenticationErrorCode.USER_NOT_ACTIVATED) {
          errors.reject("oldPass", "Tài khoản chưa được active");
        } else if (errorCode == AuthenticationErrorCode.PASSWORD_NOT_CORRECT) {
          errors.reject("oldPass", "Mật khẩu cũ không đúng");
        } else if (errorCode == AuthenticationErrorCode.DAY_FOR_CHANGE_PASS_EXPIRE) {
          errors.reject("oldPass", "Tài khoản của bạn bị khóa do quá thời gian được phép thay dổi mật khẩu. Liên hệ với quản trị để mở khóa tài khoản");
        } else if (errorCode == AuthenticationErrorCode.BEFORE_VALID_TIME) {
          errors.reject("msg.error.account.validTime.before", "Tài khoản của bạn chưa đến thời gian có hiêu lực");
        } else if (errorCode == AuthenticationErrorCode.AFTER_VALID_TIME) {
          errors.reject("msg.error.account.validTime.after", "Tài khoản của bạn đã quá thời gian có hiệu lực");
        } 
    }

    private boolean validateStrongPassword(String password) {
      if (Configuration.getInstance().isStrongPassword()) {
        String pattern = "((?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%&*()-/_=<>,.//?;:'\"\\[\\]{}`~\\\\|]).{8,})";
        return password.matches(pattern);
      } 
      return true;
    }
}
