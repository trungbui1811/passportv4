/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sendmt.MtStub;

/**
 *
 * @author TrungBH
 */
public class SmsConfig {
    private String smsUrl;
    private String smsTmpUrl;
    private String smsAcc;
    private String smsPass;
    private String smsNum;
    private static SmsConfig instance;
    private String lockingSmsUser;
    private String lockingSmsPass;
    private String lockingSmsNotChange;
    private String lockingSmsTMP;
    private MtStub stub = null;
    private final Log logger = LogFactory.getLog(getClass());

    public String getLockingSmsTMP() {
        return this.lockingSmsTMP;
    }

    public void setLockingSmsTMP(String lockingSmsTMP) {
        this.lockingSmsTMP = lockingSmsTMP;
    }

    public String getLockingSmsNotChange() {
        return this.lockingSmsNotChange;
    }

    public void setLockingSmsNotChange(String lockingSmsNotChange) {
        this.lockingSmsNotChange = lockingSmsNotChange;
    }

    public String getLockingSmsPass() {
        return this.lockingSmsPass;
    }

    public void setLockingSmsPass(String lockingSmsPass) {
        this.lockingSmsPass = lockingSmsPass;
    }

    public String getLockingSmsUser() {
        return this.lockingSmsUser;
    }

    public void setLockingSmsUser(String lockingSmsUser) {
        this.lockingSmsUser = lockingSmsUser;
    }

    private void init() {
        this.logger.info("Init SMS sender object");
        this.logger.info("Url:" + getSmsUrl());
        this.logger.info("Tmp Url:" + getSmsTmpUrl());
        this.logger.info("Acc:" + getSmsAcc());
        this.logger.info("Pas:**********");
        try {
            this.stub = new MtStub(getSmsUrl(), getSmsTmpUrl(), getSmsAcc(), getSmsPass());
        } catch (Exception e) {
            this.logger.error(e, e);
        } catch (Throwable t) {
            this.logger.error(t, t);
        } 
    }

    public int sendSms(String msisdn, String message) {
        this.logger.info("Send message " + message + " to msisdn " + msisdn);
        if (!msisdn.startsWith("84"))
            if (!msisdn.startsWith("0")) {
                msisdn = "84" + msisdn;
            } else {
                msisdn = "84" + msisdn.substring(1, msisdn.length());
            }  
        int iResult = -1;
        try {
            int iCount = 0;
            while (iCount < 3L) {
                Thread.sleep(200L);
                iResult = this.stub.send("0", "warning", getSmsNum(), msisdn, "0", message, "0");
                if (iResult == 0)
                    return iResult; 
                iCount++;
            } 
            return iResult;
        } catch (Exception e) {
            this.logger.error("Error send message " + message + " to msisdn " + msisdn, e);
            return -1;
        } 
    }

    public void sendLockingSmsTmp(long userId, String user, String msisdn) {
        DbManager db = new DbManager();
        try {
            if (getInstance().sendSms(msisdn, getInstance().getLockingSmsTMP().replace("{0}", user)) != 0) {
                db.logErrorSendSms(userId, user, getInstance().getLockingSmsTMP().replace("{0}", user), msisdn);
            } else {
                db.logSuccessSendSms(userId, user, getInstance().getLockingSmsTMP().replace("{0}", user), msisdn);
            } 
        } catch (Exception e) {
            this.logger.error("Send locking sms temp error.", e);
        } finally {
            db.close();
        } 
    }

    public void sendLockingSmsUser(long userId, String user, String msisdn) {
        DbManager db = new DbManager();
        try {
            if (getInstance().sendSms(msisdn, getInstance().getLockingSmsUser().replace("{0}", user)) != 0) {
                db.logErrorSendSms(userId, user, getInstance().getLockingSmsUser().replace("{0}", user), msisdn);
            } else {
                db.logSuccessSendSms(userId, user, getInstance().getLockingSmsUser().replace("{0}", user), msisdn);
            } 
        } catch (Exception e) {
            this.logger.error("Send locking sms user error.", e);
        } finally {
            db.close();
        } 
    }

    public void sendLockingSmsPass(long userId, String user, String msisdn) {
        DbManager db = new DbManager();
        try {
            if (getInstance().sendSms(msisdn, getInstance().getLockingSmsPass().replace("{0}", user)) != 0) {
                db.logErrorSendSms(userId, user, getInstance().getLockingSmsPass().replace("{0}", user), msisdn);
            } else {
                db.logSuccessSendSms(userId, user, getInstance().getLockingSmsPass().replace("{0}", user), msisdn);
            } 
        } catch (Exception e) {
            this.logger.error("Send locking sms user error.", e);
        } finally {
            db.close();
        } 
    }

    public void sendLockingSmsNotChange(long userId, String user, String msisdn) {
        DbManager db = new DbManager();
        try {
            if (getInstance().sendSms(msisdn, getInstance().getLockingSmsNotChange().replace("{0}", user)) != 0) {
                db.logErrorSendSms(userId, user, getInstance().getLockingSmsNotChange().replace("{0}", user), msisdn);
            } else {
                db.logSuccessSendSms(userId, user, getInstance().getLockingSmsNotChange().replace("{0}", user), msisdn);
            } 
        } catch (Exception e) {
            this.logger.error("Send locking sms user error.", e);
        } finally {
            db.close();
        } 
    }

    public static synchronized SmsConfig getInstance() {
        if (instance == null) {
            instance = new SmsConfig();
            DBConnect db = DBConnect.getInstance();
            ResourceBundle rb = ResourceBundle.getBundle("Parameter");
            instance.setSmsAcc(db.getSmsAcc());
            instance.setSmsNum(db.getSmsNum());
            instance.setSmsPass(db.getSmsPass());
            instance.setSmsTmpUrl(db.getSmsTmpUrl());
            instance.setSmsUrl(db.getSmsUrl());
            instance.setLockingSmsUser(rb.getString("LockingSmsUser"));
            instance.setLockingSmsPass(rb.getString("LockingSmsPass"));
            instance.setLockingSmsNotChange(rb.getString("LockingSmsNotChange"));
            instance.setLockingSmsTMP(rb.getString("LockingSmsTMP"));
            instance.init();
        } 
        return instance;
    }

    public String getSmsAcc() {
        return this.smsAcc;
    }

    public void setSmsAcc(String smsAcc) {
        this.smsAcc = smsAcc;
    }

    public String getSmsNum() {
        return this.smsNum;
    }

    public void setSmsNum(String smsNum) {
        this.smsNum = smsNum;
    }

    public String getSmsPass() {
        return this.smsPass;
    }

    public void setSmsPass(String smsPass) {
        this.smsPass = smsPass;
    }

    public String getSmsTmpUrl() {
        return this.smsTmpUrl;
    }

    public void setSmsTmpUrl(String smsTmpUrl) {
        this.smsTmpUrl = smsTmpUrl;
    }

    public String getSmsUrl() {
        return this.smsUrl;
    }

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public static void main(String[] args) {
        getInstance().sendSms("01649709120", "123456");
    }
}
