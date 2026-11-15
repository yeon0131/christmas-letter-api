package com.bori.christmas_letter.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class AdminKeyHasher {

    private AdminKeyHasher() {
    }

    public static String hash(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawKey.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", exception);
        }
    }

    public static boolean matches(String rawKey, String savedHash) {
        if (rawKey == null || rawKey.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(rawKey).getBytes(StandardCharsets.UTF_8),
                savedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
