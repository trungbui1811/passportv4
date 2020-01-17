/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

/**
 *
 * @author TrungBH
 */
public class UsernamePasswordCredentials implements Credentials{
    private static final long serialVersionUID = -8343864967200862794L;
    private String username;
    private String password;
    private String captcha;

    public String getCaptcha() {
        return this.captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public final String getPassword() {
        return this.password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final String getUsername() {
        return this.username;
    }

    public final void setUsername(String userName) {
        this.username = userName;
    }

    public String toString() {
        return "[username: " + this.username + "]";
    }

    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false; 
        UsernamePasswordCredentials c = (UsernamePasswordCredentials)obj;
        return (this.username.equals(c.getUsername()) && this.password.equals(c.getPassword()));
    }

    public int hashCode() {
        return this.username.hashCode() ^ this.password.hashCode();
    }
}
