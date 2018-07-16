package com.duangframework.utils;

/**
 * Created by laotang on 2017/2/23.
 */

import java.security.MessageDigest;

public class MD5 {
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public MD5() {
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        byte[] arr$ = b;
        int len$ = b.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte aB = arr$[i$];
            resultSb.append(byteToHexString(aB));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if(b < 0) {
            n = 256 + b;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin) {
        String resultString = null;

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(e.digest(origin.getBytes()));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return resultString;
    }
}

