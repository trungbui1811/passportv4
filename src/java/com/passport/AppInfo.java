/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author TrungBH
 */
public class AppInfo {
    private static String version = null;
    private static String buildNumber = null;
    private static String buildDate = null;
    private static final Log logger = LogFactory.getLog(AppInfo.class);
  
    public static String getBuildDate() {
        return buildDate;
    }

    public static String getBuildNumber() {
        return buildNumber;
    }

    public static String getVersion() {
        return version;
    }

    static {
        version = getResourceByKey("application.version");
        buildNumber = getResourceByKey("application.buildnum");
        buildDate = getResourceByKey("application.builddate");
    }

    private static String getResourceByKey(String key) {
        String result = null;
        try {
            ResourceBundle rb = ResourceBundle.getBundle("appinfo");
            result = rb.getString(key);
        } catch (Exception ex) {
            logger.error(ex, ex);
        } 
        return result;
    }

    public static void main(String[] args) {
        System.out.println("" + buildNumber);
    }
}
