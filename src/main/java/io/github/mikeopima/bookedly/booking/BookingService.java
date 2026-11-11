package io.github.mikeopima.bookedly.booking;

import io.github.mikeopima.bookedly.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

	private final SlotRepository slots;
	private final UserRepository users;

	public BookingService(SlotRepository slots, UserRepository users) {
		this.slots = slots;
		this.users = users;
	}

	/** Open days from today onwards. Held, closed and past days are left out. */
	@Transactional(readOnly = true)
	public List<SlotResponse> openSlots() {
		return slots.findByStatusAndDateGreaterThanEqualOrderByDateAsc(SlotStatus.OPEN, LocalDate.now())
				.stream().map(SlotResponse::from).toList();
	}

	/** Books the slot for the user, or explains why not (404 unknown, 409 taken/closed/past). */
	@Transactional
	public SlotResponse hold(Long slotId, Long userId) {
		int updated = slots.holdIfOpen(slotId, users.getReferenceById(userId), LocalDate.now(),
				SlotStatus.OPEN, SlotStatus.HELD);
		Slot slot = slots.findById(slotId).orElseThrow(SlotNotFoundException::new);
		if (updated == 0) {
			throw new SlotNotAvailableException();
		}
		return SlotResponse.from(slot);
	}
}
