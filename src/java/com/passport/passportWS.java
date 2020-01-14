/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 *
 * @author TrungBH
 */
public class passportWS {
    private final Log logger = LogFactory.getLog(passportWS.class);

    @WebMethod(operationName = "validate")
    public String validate(@WebParam(name = "userName") String userName, @WebParam(name = "password") String password, @WebParam(name = "domainCode") String domainCode) {
        String strResult = null;
        long start = System.currentTimeMillis();
        DbManager db = new DbManager();
        try {
            if (db.authenDomain(domainCode, userName) != 1)
                return "no"; 
            if (db.checkAccount(userName, password) != AuthenticationErrorCode.USER_PASSWORD_OK)
                return "no"; 
            Document doc = JDBCUtil.createDocument();
            doc = db.getUserData(userName, doc);
            doc = db.getRolesData(userName, doc);
            doc = db.getListObjects(userName, domainCode, doc);
            strResult = JDBCUtil.serialize(doc);
        } catch (Exception ex) {
            this.logger.error(ex, ex);
            strResult = "no";
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        long elapse = System.currentTimeMillis() - start;
        if (strResult != null && "no".equalsIgnoreCase(strResult.trim())) {
            this.logger.info("Validate failure for user [" + userName + "], domainCode [" + domainCode + "], validate time = " + elapse);
        } else {
            this.logger.info("Validate success for user [" + userName + "], domainCode [" + domainCode + "], validate time = " + elapse);
        } 
        if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to validate for user [" + userName + "], domainCode [" + domainCode + "], elapse time = " + elapse); 
        return strResult;
    }

    @WebMethod(operationName = "getAllowedApp")
    public String getAllowedApp(@WebParam(name = "userName") String userName) {
        DbManager db = new DbManager();
        try {
            Document doc = JDBCUtil.createDocument();
            doc = db.getListApp(userName, doc);
            return JDBCUtil.serialize(doc);
        } catch (Exception ex) {
            this.logger.error(ex, ex);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        return "no";
    }

    @WebMethod(operationName = "getAppFunctions")
    public String getAppFunctions(@WebParam(name = "domainCode") String domainCode) {
        DbManager db = new DbManager();
        try {
            Document doc = JDBCUtil.createDocument();
            doc = db.getAppMenu(domainCode, doc);
            return JDBCUtil.serialize(doc);
        } catch (Exception ex) {
            this.logger.error(ex, ex);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        return "no";
    }

    @WebMethod(operationName = "validateIncludeIp")
    public String validateIncludeIp(@WebParam(name = "userName") String userName, @WebParam(name = "password") String password, @WebParam(name = "domainCode") String domainCode, @WebParam(name = "ipCheck") String ipCheck) {
        String strResult = null;
        long start = System.currentTimeMillis();
        DbManager db = new DbManager();
        try {
            if (Configuration.getInstance().isIpFilter()) {
                Long checkIp = db.getCheckIp(userName);
                if (checkIp != null && checkIp.longValue() > 0L)
                    if (ipCheck == null || ipCheck.length() == 0) {
                        strResult = "no";
                    } 
            } 
            if (strResult == null)
                strResult = validate(userName, password, domainCode); 
        } catch (Exception e) {
            this.logger.error(e, e);
            strResult = "no";
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                this.logger.error("Close database connection error.", e);
            } 
        } 
        long elapse = System.currentTimeMillis() - start;
        if (strResult != null && "no".equalsIgnoreCase(strResult.trim())) {
            this.logger.info("Validate failure for user [" + userName + "], domainCode [" + domainCode + "], ip [" + ipCheck + "], validate time = " + elapse);
        } else {
            this.logger.info("Validate success for user [" + userName + "], domainCode [" + domainCode + "], ip [" + ipCheck + "] validate time = " + elapse);
        } 
        if (elapse > 60000L)
            this.logger.error("Action timeout: Too long time to validate for user [" + userName + "], domainCode [" + domainCode + "], ip [" + ipCheck + "], " + "elapse time = " + elapse); 
        return strResult;
    }
}
