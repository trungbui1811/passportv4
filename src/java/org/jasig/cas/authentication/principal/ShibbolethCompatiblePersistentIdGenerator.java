/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.authentication.principal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.inspektr.common.ioc.annotation.NotNull;
import org.springframework.webflow.util.Base64;

/**
 *
 * @author TrungBH
 */
public final class ShibbolethCompatiblePersistentIdGenerator implements PersistentIdGenerator{
    private static final byte CONST_SEPARATOR = 33;

    private Base64 base64 = new Base64();

    @NotNull
    private byte[] salt;

    public String generate(Principal principal, Service service) {
      try {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(service.getId().getBytes());
        md.update((byte)33);
        md.update(principal.getId().getBytes());
        md.update((byte)33);
        return this.base64.encodeToString(md.digest(this.salt)).replaceAll(System.getProperty("line.separator"), "");
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      } 
    }

    public void setSalt(String salt) {
      this.salt = salt.getBytes();
    }
}
