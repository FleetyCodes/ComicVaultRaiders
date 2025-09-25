package com.comicvaultraiders.comicvaultraiders.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${secret.key}")
    private String secretKeyString;

    public static String encrypt(String value, String key) throws GeneralSecurityException {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));
    }

    public static String decrypt(String value, String key) throws GeneralSecurityException  {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(value)));
    }

    public String getSecretKeyString(){
        return secretKeyString;
    }

}
