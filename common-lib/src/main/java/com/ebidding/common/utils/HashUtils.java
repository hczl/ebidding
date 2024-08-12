package com.ebidding.common.utils;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HashUtils {
    private static final String SECRET="123456";
    public static String encode(String password) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Hex.toHexString(mac.doFinal(password.getBytes(StandardCharsets.UTF_8)));
    }

}
