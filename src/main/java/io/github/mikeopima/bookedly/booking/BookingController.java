package io.github.mikeopima.bookedly.booking;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Any logged-in user can see open days and book one. */
@RestController
@RequestMapping("/api")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	/** Days that can still be booked. */
	@GetMapping("/slots")
	public List<SlotResponse> openSlots() {
		return bookingService.openSlots();
	}

	/** Book a day: {"slotId": 12}. The logged-in user becomes the booker. */
	@PostMapping("/bookings")
	public ResponseEntity<SlotResponse> book(@Valid @RequestBody BookingRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		SlotResponse booked = bookingService.hold(request.slotId(), Long.valueOf(jwt.getSubject()));
		return ResponseEntity.status(HttpStatus.CREATED).body(booked);
	}
}
