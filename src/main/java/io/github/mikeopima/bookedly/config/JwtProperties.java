package io.github.mikeopima.bookedly.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings under "bookedly.jwt" in application.properties.
 *
 * @param secret HMAC signing key; must be at least 32 bytes for HS256
 * @param ttl    how long a token stays valid
 * @param issuer value of the "iss" claim
 */
@ConfigurationProperties(prefix = "bookedly.jwt")
public record JwtProperties(String secret, Duration ttl, String issuer) {

	public JwtProperties {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("bookedly.jwt.secret must be at least 32 bytes (set JWT_SECRET)");
		}
		if (ttl == null) {
			ttl = Duration.ofHours(1);
		}
		if (issuer == null || issuer.isBlank()) {
			issuer = "bookedly";
		}
	}
}
