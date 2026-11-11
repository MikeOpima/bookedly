package io.github.mikeopima.bookedly.booking;

import java.time.LocalDate;

/** What the API returns about a slot. */
public record SlotResponse(Long id, LocalDate date, String status, Long bookedByUserId) {

	public static SlotResponse from(Slot slot) {
		Long bookedBy = slot.getBookedBy() == null ? null : slot.getBookedBy().getId();
		return new SlotResponse(slot.getId(), slot.getDate(), slot.getStatus().name(), bookedBy);
	}
}
