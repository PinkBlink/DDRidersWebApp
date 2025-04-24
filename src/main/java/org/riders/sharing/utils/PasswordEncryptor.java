package org.riders.sharing.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    private static final String ALGORITHM_FOR_PASS = "SHA-256";

    public static String encryptPassword(String password) {
        return toHexString(computeSHA256(password));
    }

    private static byte[] computeSHA256(String password) {
        try {
            final var messageDigest = MessageDigest.getInstance(ALGORITHM_FOR_PASS);
            return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHexString(byte[] hash) {
        final var hexString = new StringBuilder(2 * hash.length);
        for (final var byteHash : hash) {
            final var hex = Integer.toHexString(0xff & byteHash);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
