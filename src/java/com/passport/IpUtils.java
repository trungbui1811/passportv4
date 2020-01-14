/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author TrungBH
 */
public class IpUtils {
    public static final int IS_VALID = 0;
    public static final int INVALID = 1;
    public static final int INVALID_CHARACTER = 2;
    public static final int INVALID_FORMAT = 3;
    static final Logger logger = Logger.getLogger(IpUtils.class);

    public static int validateCharacters(String ipRegex) {
        String expression = "^[0-9\\.,*\\[\\]\\|-]*$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(ipRegex);
        return matcher.matches() ? 0 : 2;
    }

    public static int validateOctet(String octet) {
        if (octet == null || octet.length() == 0)
            return 3; 
        if (octet.length() > 1 && octet.charAt(0) == '0')
            return 3; 
        int value = -1;
        try {
            value = Integer.parseInt(octet);
        } catch (Exception e) {
            logger.error(e);
            return 3;
        } 
        if (value < 0 || value > 255)
            return 1; 
        return 0;
    }

    public static int validateOctetRegex(String octet) {
        if (octet == null || octet.length() < 1)
            return 3; 
        if (!"*".equalsIgnoreCase(octet))
            if (octet.charAt(0) == '[' && octet.charAt(octet.length() - 1) == ']') {
                octet = octet.substring(1, octet.length() - 1);
                if (octet.length() < 1)
                    return 3; 
                int pos = octet.indexOf("-");
                if (pos >= 0) {
                    if (octet.charAt(0) == '-' || octet.charAt(octet.length() - 1) == '-')
                        return 3; 
                    String[] nums = octet.split("-");
                    if (nums.length != 2)
                        return 3; 
                    if (validateOctet(nums[0]) != 0 || validateOctet(nums[1]) != 0)
                        return 3; 
                    try {
                        int begin = Integer.parseInt(nums[0]);
                        int end = Integer.parseInt(nums[1]);
                        if (begin >= end)
                            return 3; 
                    } catch (Exception e) {
                        logger.error(e);
                        return 3;
                    } 
                } else {
                    if (octet.charAt(0) == ',' || octet.charAt(octet.length() - 1) == ',')
                        return 3; 
                    String[] nums = octet.split(",");
                    if (nums.length < 2)
                        return 3; 
                    for (int j = 0; j < nums.length; j++) {
                        String num = nums[j];
                        if (validateOctet(num) != 0)
                            return 3; 
                    } 
                } 
            } else if (validateOctet(octet) != 0) {
                return 3;
            }  
        return 0;
    }

    public static int matchOctet(String octet, String octetRegex) {
        if ("*".equalsIgnoreCase(octetRegex))
            return 0; 
        if (octetRegex.charAt(0) == '[' && octetRegex.charAt(octetRegex.length() - 1) == ']') {
            octetRegex = octetRegex.substring(1, octetRegex.length() - 1);
            int pos = octetRegex.indexOf("-");
            if (pos >= 0) {
                String[] nums = octetRegex.split("-");
                try {
                    int begin = Integer.parseInt(nums[0]);
                    int end = Integer.parseInt(nums[1]);
                    int value = Integer.parseInt(octet);
                    if (value >= begin && value <= end)
                        return 0; 
                } catch (Exception e) {
                    logger.error(e);
                    return 1;
                } 
            } else {
                String[] nums = octetRegex.split(",");
                for (int j = 0; j < nums.length; j++) {
                    String num = nums[j];
                    if (num.equalsIgnoreCase(octet))
                        return 0; 
                } 
            } 
        } else if (octetRegex.equalsIgnoreCase(octet)) {
            return 0;
        } 
        return 1;
    }

    public static int validateIpRegex(String ipRegex) {
        if (ipRegex == null || ipRegex.length() < 1)
            return 3; 
        int iResult = validateCharacters(ipRegex);
        if (iResult != 0)
            return iResult; 
        if (ipRegex.charAt(0) == '|' || ipRegex.charAt(ipRegex.length() - 1) == '|')
            return 3; 
        String[] regexes = ipRegex.split("\\|");
        for (int i = 0; i < regexes.length; i++) {
            String regex = regexes[i];
            if (regex == null || regex.length() < 1)
                return 3; 
            if (regex.charAt(0) == '.' || regex.charAt(regex.length() - 1) == '.')
                return 3; 
            String[] octets = regex.split("\\.");
            if (octets.length != 4)
                return 3; 
            if (validateOctet(octets[0]) != 0 || validateOctet(octets[1]) != 0 || validateOctet(octets[2]) != 0)
                return 3; 
            if (validateOctetRegex(octets[3]) != 0)
                return 3; 
        } 
        return 0;
    }

    public static int matchIp(String ip, String ipRegex) {
        if (ip == null || ip.length() < 1 || ipRegex == null || ipRegex.length() < 1)
            return 3; 
        int iResult = validateIpRegex(ipRegex);
        if (iResult != 0)
            return iResult; 
        String[] regexes = ipRegex.split("\\|");
        for (int i = 0; i < regexes.length; i++) {
            String regex = regexes[i];
            String[] octets = regex.split("\\.");
            String[] ots = ip.split("\\.");
            if (octets.length != 4 || ots.length != 4)
                return 3; 
            if ((ots[0] + ots[1] + ots[2]).equalsIgnoreCase(octets[0] + octets[1] + octets[2]) && 
                matchOctet(ots[3], octets[3]) == 0)
                return 0; 
        } 
        return 1;
    }

    public static int matchAllowIp(String ip, String ipRegex) {
        if (ip == null || ip.length() < 1 || ipRegex == null || ipRegex.length() < 1)
            return 3; 
        int iResult = validateCharacters(ipRegex);
        if (iResult != 0)
            return iResult; 
        String[] regexes = ipRegex.split("\\|");
        for (int i = 0; i < regexes.length; i++) {
            String regex = regexes[i];
            String[] octets = regex.split("\\.");
            String[] ots = ip.split("\\.");
            if (matchOctet(ots[0], octets[0]) == 0 && matchOctet(ots[1], octets[1]) == 0 && matchOctet(ots[2], octets[2]) == 0 && matchOctet(ots[3], octets[3]) == 0)
                return 0; 
        } 
        return 1;
    }
}
