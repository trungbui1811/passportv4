package com.passport;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TrungBH
 */
public class ModifyHeaderUtils {
    static final Logger logger = Logger.getLogger(ModifyHeaderUtils.class);
    private static Base64 base64 = new Base64();
    private static Hex hex = new Hex();
    private static String PADDING_CHAR = "@";
    
    public static String encodeBase64(byte[] input) {
        String tmp = new String(base64.encode(input));
        return tmp.replaceAll("\r\n", "");
    }

    public static byte[] decodeBase64(String input) {
        return base64.decode(input.getBytes());
    }

    public static String toHex(byte[] input) {
        return (new String(hex.encode(input))).toUpperCase();
    }

    public static byte[] toByte(String input) throws Exception {
        try {
            return hex.decode(input.getBytes());
        } catch (DecoderException e) {
            logger.error(e);
            throw new Exception("invalid input hex string");
        } 
    }

    public static byte[] encryptWithAES(String input) throws Exception {
        try {
            byte[] key = "kengkengkengkeng".getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            byte[] initialVector = { 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 0, 0 };
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(1, skeySpec, new IvParameterSpec(initialVector));
            return cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            throw new Exception("encrypt error", e);
        } 
    }

    public static byte[] encryptWithAES(byte[] input) throws Exception {
        try {
            byte[] key = "kengkengkengkeng".getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            byte[] initialVector = { 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 0, 0 };
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(1, skeySpec, new IvParameterSpec(initialVector));
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new Exception("encrypt error", e);
        } 
    }

    public static String decodeWithAES(byte[] input) throws Exception {
        try {
            byte[] key = "kengkengkengkeng".getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            byte[] initialVector = { 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 0, 0 };
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, skeySpec, new IvParameterSpec(initialVector));
            return new String(cipher.doFinal(input));
        } catch (Exception e) {
            throw new Exception("encrypt error", e);
        } 
    }

    public static String parseModifyHeader(String input) {
        String outputStr = null;
        try {
            byte[] outputByte = decodeBase64(input);
            outputStr = decodeWithAES(outputByte);
            if (outputStr == null || outputStr.trim().length() == 0) {
                outputStr = null;
            } else {
                outputStr = outputStr.replaceAll(PADDING_CHAR, "");
                if ("null".equalsIgnoreCase(outputStr.trim()))
                    outputStr = null; 
            } 
        } catch (Exception e) {
            logger.error(e);
            outputStr = null;
        } 
        return outputStr;
    }

    public static String parseIP(String input) {
        String ip = parseModifyHeader(input);
        if (ip != null && ip.length() > 0) {
            int pos = 0;
            while (pos < ip.length() && (ip.charAt(pos) == '.' || (ip.charAt(pos) >= '0' && ip.charAt(pos) <= '9')))
                pos++; 
            if (pos < ip.length())
                ip = ip.substring(0, pos); 
            String[] octets = ip.split("\\.");
            if (octets.length >= 4)
                try {
                    int value = Integer.parseInt(octets[3]);
                    if (value > 255) {
                        octets[3] = octets[3].substring(0, 2);
                        ip = octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
                    } 
                } catch (Exception e) {
                  logger.error(e);
                }  
        } 
        return ip;
    }

    public static String parseMAC(String input) {
        return parseModifyHeader(input);
    }

    public static String parseVersion(String input) {
        return parseModifyHeader(input);
    }

    public static boolean checkValidVersion(String v1, String v2) {
        int arrayLength = 2;
        String[] array1 = v1.split("-", 2);
        String[] array2 = v2.split("-", 2);
        if (array1.length == arrayLength && array2.length == arrayLength && array1[1].equalsIgnoreCase(array2[1]) && array1[1].compareToIgnoreCase(array2[1]) >= 0)
            return true; 
        return false;
    }

    public static boolean isValidVersion(String versionCheck, String[] versions) {
        if (versions == null || versions.length == 0)
            return false; 
        for (int i = 0; i < versions.length; i++) {
            String version = versions[i];
            if (checkValidVersion(versionCheck, version))
                return true; 
        } 
        return false;
    }

    public static boolean isValidVersion(String versionCheck, String versions) {
        if (versions == null)
            return false; 
        String[] array = versions.split(",");
        return isValidVersion(versionCheck, array);
    }
}
