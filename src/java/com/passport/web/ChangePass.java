/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.web;

/**
 *
 * @author TrungBH
 */
public class ChangePass {
    private String oldPass;
    private String newPass;
    private String retypedPass;
    private String userName;

    public String getOldPass() {
        return this.oldPass;
    }

    public void setOldPass(String oldPass) {
        if (oldPass != null) {
            this.oldPass = oldPass.trim();
        } else {
            this.oldPass = null;
        } 
    }

    public String getNewPass() {
        return this.newPass;
    }

    public void setNewPass(String newPass) {
        if (newPass != null) {
            this.newPass = newPass.trim();
        } else {
            this.newPass = null;
        } 
    }

    public String getRetypedPass() {
        return this.retypedPass;
    }

    public void setRetypedPass(String retypedPass) {
        if (retypedPass != null) {
            this.retypedPass = retypedPass.trim();
        } else {
            this.retypedPass = null;
        } 
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        if (userName != null) {
            this.userName = userName.trim();
        } else {
            this.userName = null;
        } 
    }
}
