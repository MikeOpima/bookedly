package io.github.mikeopima.bookedly.booking;

import jakarta.validation.constraints.NotNull;

/** Body of POST /api/bookings: which slot (day) to book. */
public record BookingRequest(@NotNull Long slotId) {
}
