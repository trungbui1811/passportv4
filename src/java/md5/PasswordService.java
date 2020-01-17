/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package md5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author TrungBH
 */
public final class PasswordService {
    protected final Log logger = LogFactory.getLog(getClass());
    private static md5.PasswordService instance;
    public synchronized String encrypt1(String plaintext) throws Exception {
        MessageDigest md = null;
        String hash = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(plaintext.getBytes("UTF-8"));
            byte[] raw = md.digest();
            hash = (new BASE64Encoder()).encode(raw);
            (new BASE64Decoder()).decodeBuffer(hash);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            this.logger.error(e, e);
        } catch (Exception e) {
            this.logger.error(e, e);
        } 
        return hash;
    }

    public synchronized String encrypt2(String plaintext) throws Exception {
        MessageDigest md = null;
        String hash = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            this.logger.info("plaintext " + plaintext);
            md.update(plaintext.getBytes("UTF-8"));
            byte[] raw = md.digest();
            hash = (new BASE64Encoder()).encode(raw);
            this.logger.info("hash " + hash);
            (new BASE64Decoder()).decodeBuffer(hash);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            this.logger.error(e, e);
        } catch (Exception e) {
            this.logger.error(e, e);
        } 
        return hash;
    }

    public synchronized String encrypt1(String username, String password) throws Exception {
        String hash = null;
        try {
            this.logger.info("username " + username);
            hash = encrypt1(username);
            this.logger.info("hash " + hash);
            hash = hash + password;
            this.logger.info("hash " + hash);
            hash = encrypt2(hash);
            this.logger.info("hash " + hash);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            this.logger.error(e, e);
        } catch (Exception e) {
            this.logger.error(e, e);
        } 
        return hash;
    }

    public static synchronized md5.PasswordService getInstance() {
        if (instance == null)
            instance = new md5.PasswordService(); 
        return instance;
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.encodeBase64String(hash);
    }

    public synchronized String encrypt(String plaintext) throws Exception {
        MessageDigest md = null;
        String hash = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(plaintext.getBytes("UTF-8"));
            byte[] raw = md.digest();
            hash = (new BASE64Encoder()).encode(raw);
            (new BASE64Decoder()).decodeBuffer(hash);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(e, e);
        } catch (UnsupportedEncodingException e) {
            this.logger.error(e, e);
        } catch (Exception e) {
            this.logger.error(e, e);
        } 
        return hash;
    }
}
