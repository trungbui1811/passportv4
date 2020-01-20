/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas;

/**
 *
 * @author TrungBH
 */
public final class CasVersion {
    public static String getVersion() {
        return CasVersion.class.getPackage().getImplementationVersion();
    }
}
