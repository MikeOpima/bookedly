package io.github.mikeopima.bookedly.booking;

import io.github.mikeopima.bookedly.config.AdminProperties;
import io.github.mikeopima.bookedly.user.User;
import io.github.mikeopima.bookedly.user.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fills the slots table with one slot per day of the current month, if they aren't there yet.
 * The first time a month is created, a few upcoming days are marked as held by the
 * first admin, so there is something to test against.
 * Runs after AdminSeeder so that admin already exists.
 */
@Component
@Order(2)
class SlotSeeder implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(SlotSeeder.class);
	private static final int HELD_EXAMPLES = 3;

	private final SlotRepository slots;
	private final UserRepository users;
	private final AdminProperties admin;

	SlotSeeder(SlotRepository slots, UserRepository users, AdminProperties admin) {
		this.slots = slots;
		this.users = users;
		this.admin = admin;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		LocalDate today = LocalDate.now();
		YearMonth month = YearMonth.from(today);
		List<Slot> created = new ArrayList<>();
		for (int day = 1; day <= month.lengthOfMonth(); day++) {
			LocalDate date = month.atDay(day);
			if (!slots.existsByDate(date)) {
				created.add(new Slot(date));
			}
		}
		if (created.isEmpty()) {
			return;
		}
		findAdmin().ifPresent(adminUser -> created.stream()
				.filter(slot -> slot.getDate().isAfter(today))
				.filter(slot -> slot.getDate().getDayOfMonth() % 3 == 0)
				.limit(HELD_EXAMPLES)
				.forEach(slot -> slot.holdFor(adminUser)));
		slots.saveAll(created);
		log.info("Created {} slots for {}", created.size(), month);
	}

	private Optional<User> findAdmin() {
		if (admin.email() == null || admin.email().isBlank()) {
			return Optional.empty();
		}
		return users.findByEmail(admin.email().trim().toLowerCase(Locale.ROOT));
	}
}
