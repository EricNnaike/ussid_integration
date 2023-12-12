package com.example.mfb_ussd_process_flow;

import com.example.mfb_ussd_process_flow.config.JwtService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.security.Key;

@SpringBootApplication
@EnableAsync
public class MfbUssdProcessFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MfbUssdProcessFlowApplication.class, args);
    }


    public static String generateRandomKey() {
        // Use Keys.secretKeyFor to generate a new random key
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

        // Convert the byte array to a hexadecimal string
        return bytesToHex(keyBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
