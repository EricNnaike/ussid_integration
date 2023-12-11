package com.example.mfb_ussd_process_flow.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@UtilityClass
public class AppUtils {
    public String generateRequestID(int length) {
        String characters = "0123456789";
        StringBuilder randomString = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }

        return randomString.toString();
    }

    public static String Sha512(String debitAccount, String creditAccount, BigDecimal payAmount, String requestId, String secretKey) {
        String dataToHash = debitAccount + creditAccount + payAmount + requestId + secretKey;
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //  md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;

    }

}
