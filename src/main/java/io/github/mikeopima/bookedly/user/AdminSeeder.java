package io.github.mikeopima.bookedly.user;

import io.github.mikeopima.bookedly.config.AdminProperties;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Makes sure the first admin from "bookedly.admin" exists when the app starts.
 * Without it nobody could promote anyone, because promoting needs an admin.
 */
@Component
@Order(1)
class AdminSeeder implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

	private final AdminProperties props;
	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;

	AdminSeeder(AdminProperties props, UserRepository users, PasswordEncoder passwordEncoder) {
		this.props = props;
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (props.email() == null || props.email().isBlank()) {
			log.info("No bookedly.admin.email set, skipping first-admin setup");
			return;
		}
		String email = props.email().trim().toLowerCase(Locale.ROOT);
		User admin = users.findByEmail(email)
				.orElseGet(() -> new User(email, passwordEncoder.encode(props.password()), props.fullName()));
		if (admin.getRole() != Role.ADMIN) {
			admin.promoteToAdmin();
		}
		users.save(admin);
		log.info("First admin account ready: {}", email);
	}
}
