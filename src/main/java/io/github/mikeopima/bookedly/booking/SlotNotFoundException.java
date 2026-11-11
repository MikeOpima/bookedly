package io.github.mikeopima.bookedly.booking;

public class SlotNotFoundException extends RuntimeException {

	public SlotNotFoundException() {
		super("Slot not found");
	}
}
