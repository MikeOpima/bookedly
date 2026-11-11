package io.github.mikeopima.bookedly.booking;

public class SlotNotAvailableException extends RuntimeException {

	public SlotNotAvailableException() {
		super("This date is no longer available");
	}
}
