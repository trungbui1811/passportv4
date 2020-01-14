/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author TrungBH
 */
public class Configuration {
    private static com.passport.Configuration instance;
    private boolean balancerUsed = false;
    private boolean ipFilter = false;
    private boolean ipLanFilter = false;
    private String modifyHeaderVersions = null;
    private Integer maxConnectionIdle = null;
    private Integer minConnectionIdle = null;
    private Integer maxConnectionActive = null;
    private String jdbcDriver = null;
    private boolean strongPassword = false;
    private boolean captchaUsed = false;
    private boolean whiteList = false;

    public boolean isWhiteList() {
        return this.whiteList;
    }

    private static final Log logger = LogFactory.getLog(EncryptDecryptUtils.class);

    public void setWhiteList(boolean whiteList) {
        this.whiteList = whiteList;
    }

    public boolean isCaptchaUsed() {
        return this.captchaUsed;
    }

    public void setCaptchaUsed(boolean captchaUsed) {
        this.captchaUsed = captchaUsed;
    }

    public boolean isStrongPassword() {
        return this.strongPassword;
    }

    public void setStrongPassword(boolean strongPassword) {
        this.strongPassword = strongPassword;
    }

    public Integer getMinConnectionIdle() {
        return this.minConnectionIdle;
    }

    public void setMinConnectionIdle(Integer minConnectionIdle) {
        this.minConnectionIdle = minConnectionIdle;
    }

    public String getJdbcDriver() {
        return this.jdbcDriver;
    }

    public void setJdbcDriver(String JdbcDriver) {
        this.jdbcDriver = JdbcDriver;
    }

    public Integer getMaxConnectionActive() {
        return this.maxConnectionActive;
    }

    public void setMaxConnectionActive(Integer maxConnectionActive) {
        this.maxConnectionActive = maxConnectionActive;
    }

    public Integer getMaxConnectionIdle() {
        return this.maxConnectionIdle;
    }

    public void setMaxConnectionIdle(Integer maxConnectionIdle) {
        this.maxConnectionIdle = maxConnectionIdle;
    }

    public String getModifyHeaderVersions() {
        return this.modifyHeaderVersions;
    }

    public void setModifyHeaderVersions(String modifyHeaderVersions) {
        this.modifyHeaderVersions = modifyHeaderVersions;
    }

    public boolean isIpLanFilter() {
        return this.ipLanFilter;
    }

    public void setIpLanFilter(boolean ipLanFilter) {
        this.ipLanFilter = ipLanFilter;
    }

    public boolean isIpFilter() {
        return this.ipFilter;
    }

    public void setIpFilter(boolean ipFilter) {
        this.ipFilter = ipFilter;
    }

    public boolean isBalancerUsed() {
        return this.balancerUsed;
    }

    public void setBalancerUsed(boolean balancerUsed) {
        this.balancerUsed = balancerUsed;
    }

    public boolean isSendSms() {
      return (DBConnect.getInstance().getSmsUrl() != null && DBConnect.getInstance().getSmsUrl().length() > 0);
    }

    public static com.passport.Configuration getInstance() {
        if (instance == null)
            instance = new com.passport.Configuration(); 
        return instance;
    }

    public Configuration() {
        if ("TRUE".equals(ResourceBundleUtils.getResource("BALANCER_USED")))
            this.balancerUsed = true; 
        if ("TRUE".equals(ResourceBundleUtils.getResource("CAPTCHA_USED")))
            this.captchaUsed = true; 
        if ("TRUE".equals(ResourceBundleUtils.getResource("WHITE_LIST")))
            this.whiteList = true; 
        if ("TRUE".equals(ResourceBundleUtils.getResource("IP_FILTER")))
            this.ipFilter = true; 
        if ("TRUE".equals(ResourceBundleUtils.getResource("IP_LAN_FILTER")))
            this.ipLanFilter = true; 
        if ("TRUE".equals(ResourceBundleUtils.getResource("STRONG_PASSWORD")))
            this.strongPassword = true; 
        this.modifyHeaderVersions = ResourceBundleUtils.getResource("HEADER_VERSION");
        this.jdbcDriver = ResourceBundleUtils.getResource("DRIVER");
        try {
            this.maxConnectionIdle = Integer.valueOf(Integer.parseInt(ResourceBundleUtils.getResource("MAX_CONNECTION_IDLE")));
        } catch (Exception e) {
            logger.error(e, e);
            this.maxConnectionIdle = null;
        } 
        try {
            this.minConnectionIdle = Integer.valueOf(Integer.parseInt(ResourceBundleUtils.getResource("MIN_CONNECTION_IDLE")));
        } catch (Exception e) {
            logger.error(e, e);
            this.minConnectionIdle = null;
        } 
        try {
            this.maxConnectionActive = Integer.valueOf(Integer.parseInt(ResourceBundleUtils.getResource("MAX_CONN")));
        } catch (Exception e) {
            logger.error(e, e);
            this.maxConnectionActive = null;
        } 
    }
}
