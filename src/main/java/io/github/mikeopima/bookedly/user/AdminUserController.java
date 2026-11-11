package io.github.mikeopima.bookedly.user;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only user management. Customers get 403, no token gets 401.
 *
 * A promoted user's current token still says CUSTOMER, because the role is
 * written into the token when it's issued. They get admin access after
 * logging in again.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final UserService userService;

	public AdminUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<UserResponse> list() {
		return userService.listAll();
	}

	@PostMapping("/{id}/promote")
	public ResponseEntity<UserResponse> promote(@PathVariable Long id) {
		return userService.promoteToAdmin(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
