package hello.pet.authservice.domain.entity;

import hello.pet.authservice.adapter.out.security.TokenHash;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_user", columnList = "userId"),
        @Index(name = "idx_refresh_token_revoked", columnList = "revoked")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String tokenHash;
    private Instant expiresAt;

    private boolean revoked;
    private String replacedBy;

    private Instant createdAt;

    public static RefreshToken issue(Long userId, String rawToken, long refreshExpMs) {
        return new RefreshToken(
                null,
                userId,
                TokenHash.sha256(rawToken),
                Instant.now().plusMillis(refreshExpMs),
                false,
                null,
                Instant.now()
        );
    }

    public void revoked() {
        this.revoked = true;
        this.replacedBy = null;
    }
}
