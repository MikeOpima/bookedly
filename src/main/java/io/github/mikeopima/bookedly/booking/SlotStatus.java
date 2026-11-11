package io.github.mikeopima.bookedly.booking;

/** A slot is one whole day that can be booked. */
public enum SlotStatus {
	/** Free to book (as long as the date hasn't passed). */
	OPEN,
	/** Someone has booked it. */
	HELD,
	/** Taken off the calendar (for example by an admin). */
	CLOSED
}
