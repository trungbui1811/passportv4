/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.util.ResourceBundle;

/**
 *
 * @author TrungBH
 */
public class ResourceBundleUtils {
    private static ResourceBundle rb = null;

    public static String getResource(String key) {
        if (rb == null)
            rb = ResourceBundle.getBundle("Parameter"); 
        if (rb != null)
            return rb.getString(key); 
        return "";
    }

    public static String getResource(String bundle, String key) {
        if (rb == null)
            rb = ResourceBundle.getBundle(bundle); 
        if (rb != null)
            return rb.getString(key); 
        return "";
    }
}
