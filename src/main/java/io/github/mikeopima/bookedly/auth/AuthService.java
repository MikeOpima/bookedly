package io.github.mikeopima.bookedly.auth;

import io.github.mikeopima.bookedly.user.User;
import io.github.mikeopima.bookedly.user.UserRepository;
import io.github.mikeopima.bookedly.user.UserResponse;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokens;
	/** Used to spend the same time on unknown emails as on wrong passwords. */
	private final String dummyHash;

	public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenService tokens) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.tokens = tokens;
		this.dummyHash = passwordEncoder.encode("not-a-real-password");
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalize(request.email());
		if (users.existsByEmail(email)) {
			throw new EmailAlreadyUsedException();
		}
		User user = new User(email, passwordEncoder.encode(request.password()), request.fullName().trim());
		try {
			user = users.saveAndFlush(user);
		} catch (DataIntegrityViolationException e) {
			// Two sign-ups with the same email at the same moment: the unique constraint catches it
			throw new EmailAlreadyUsedException();
		}
		return toResponse(user);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		User user = users.findByEmail(normalize(request.email())).orElse(null);
		if (user == null) {
			passwordEncoder.matches(request.password(), dummyHash);
			throw new InvalidCredentialsException();
		}
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}
		return toResponse(user);
	}

	private AuthResponse toResponse(User user) {
		return new AuthResponse(tokens.issue(user), "Bearer", tokens.ttlSeconds(), UserResponse.from(user));
	}

	private static String normalize(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
