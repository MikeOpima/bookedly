package io.github.mikeopima.bookedly.auth;

import io.github.mikeopima.bookedly.user.UserRepository;
import io.github.mikeopima.bookedly.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final UserRepository users;

	public AuthController(AuthService authService, UserRepository users) {
		this.authService = authService;
		this.users = users;
	}

	/** Sign up. Returns a token straight away so the user is logged in. */
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	/** Log in with email + password, get a token back. */
	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	/** Who am I? Requires "Authorization: Bearer <token>". */
	@GetMapping("/me")
	public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
		return users.findById(Long.valueOf(jwt.getSubject()))
				.map(UserResponse::from)
				.map(ResponseEntity::ok)
				// token is valid but the account was deleted since it was issued
				.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}
}
