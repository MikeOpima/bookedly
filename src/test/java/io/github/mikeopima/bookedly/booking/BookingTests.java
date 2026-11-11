package io.github.mikeopima.bookedly.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingTests {

	/** Each test makes its own slots on dates nobody else uses. */
	private static final AtomicInteger DAY = new AtomicInteger();

	@Autowired
	MockMvc mvc;

	@Autowired
	SlotRepository slots;

	private static LocalDate futureDate() {
		return LocalDate.of(2100, 1, 1).plusDays(DAY.incrementAndGet());
	}

	private static LocalDate pastDate() {
		return LocalDate.of(2000, 1, 1).plusDays(DAY.incrementAndGet());
	}

	private Slot slotOn(LocalDate date) {
		return slots.save(new Slot(date));
	}

	/** Signs up a new customer and returns their login response. */
	private String signUp() throws Exception {
		return mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"user-%s@example.com","password":"password123","fullName":"Test User"}"""
								.formatted(UUID.randomUUID())))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
	}

	private static String bookingJson(Object slotId) {
		return """
				{"slotId":%s}""".formatted(slotId);
	}

	@Test
	void currentMonthIsSeededAtStartup() {
		assertThat(slots.existsByDate(LocalDate.now())).isTrue();
	}

	@Test
	void listsOnlyOpenDatesFromTodayOn() throws Exception {
		String token = JsonPath.read(signUp(), "$.token");
		Slot open = slotOn(futureDate());
		Slot past = slotOn(pastDate());
		Slot taken = slotOn(futureDate());
		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(taken.getId())))
				.andExpect(status().isCreated());

		mvc.perform(get("/api/slots").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == %d)].status".formatted(open.getId())).value("OPEN"))
				.andExpect(jsonPath("$[?(@.id == %d)]".formatted(past.getId())).doesNotExist())
				.andExpect(jsonPath("$[?(@.id == %d)]".formatted(taken.getId())).doesNotExist());
	}

	@Test
	void bookingASlotHoldsItForThatUser() throws Exception {
		String body = signUp();
		String token = JsonPath.read(body, "$.token");
		Integer userId = JsonPath.read(body, "$.user.id");
		Slot slot = slotOn(futureDate());

		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(slot.getId())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(slot.getId().intValue()))
				.andExpect(jsonPath("$.status").value("HELD"))
				.andExpect(jsonPath("$.bookedByUserId").value(userId));
	}

	@Test
	void aSlotCannotBeBookedTwice() throws Exception {
		String first = JsonPath.read(signUp(), "$.token");
		String second = JsonPath.read(signUp(), "$.token");
		Slot slot = slotOn(futureDate());

		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + first)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(slot.getId())))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + second)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(slot.getId())))
				.andExpect(status().isConflict());
	}

	@Test
	void pastDatesCannotBeBooked() throws Exception {
		String token = JsonPath.read(signUp(), "$.token");
		Slot past = slotOn(pastDate());

		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(past.getId())))
				.andExpect(status().isConflict());
	}

	@Test
	void unknownSlotIs404AndMissingSlotIdIs400() throws Exception {
		String token = JsonPath.read(signUp(), "$.token");

		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(bookingJson(999999)))
				.andExpect(status().isNotFound());
		mvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void bookingEndpointsNeedAToken() throws Exception {
		mvc.perform(get("/api/slots")).andExpect(status().isUnauthorized());
		mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(bookingJson(1)))
				.andExpect(status().isUnauthorized());
	}
}
