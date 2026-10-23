package io.github.mikeopima.bookedly.auth;

import io.github.mikeopima.bookedly.config.JwtProperties;
import io.github.mikeopima.bookedly.user.User;
import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Creates signed JWTs for users. */
@Service
public class TokenService {

	private final JwtEncoder encoder;
	private final JwtProperties props;

	public TokenService(JwtEncoder encoder, JwtProperties props) {
		this.encoder = encoder;
		this.props = props;
	}

	public String issue(User user) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(props.issuer())
				.issuedAt(now)
				.expiresAt(now.plus(props.ttl()))
				.subject(String.valueOf(user.getId())) // "sub" = user id
				.claim("email", user.getEmail())
				.claim("roles", List.of(user.getRole().name()))
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long ttlSeconds() {
		return props.ttl().toSeconds();
	}
}
