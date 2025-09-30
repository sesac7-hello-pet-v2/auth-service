package hello.pet.authservice.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TokenHash {
    private TokenHash() {}
    public static String sha256(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] digests = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digests) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
