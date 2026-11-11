package io.github.mikeopima.bookedly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings under "bookedly.admin": the first admin account, created at startup
 * if it doesn't exist yet. That admin can then promote other users.
 * Leave the email blank to skip creating it.
 *
 * @param email    login email for the first admin
 * @param password login password for the first admin (8+ characters)
 * @param fullName display name for the first admin
 */
@ConfigurationProperties(prefix = "bookedly.admin")
public record AdminProperties(String email, String password, String fullName) {

	public AdminProperties {
		if (email != null && !email.isBlank() && (password == null || password.length() < 8)) {
			throw new IllegalStateException("bookedly.admin.password must be at least 8 characters (set ADMIN_PASSWORD)");
		}
		if (fullName == null || fullName.isBlank()) {
			fullName = "Administrator";
		}
	}
}
