package io.github.mikeopima.bookedly.booking;

import io.github.mikeopima.bookedly.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * One bookable day. There is at most one slot per date.
 * Dates in the past count as closed/expired even if their status is still OPEN.
 */
@Entity
@Table(name = "slots")
public class Slot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "slot_date", nullable = false, unique = true)
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SlotStatus status = SlotStatus.OPEN;

	/** Who booked it; empty while the slot is open. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booked_by_user_id")
	private User bookedBy;

	protected Slot() {
		// for JPA
	}

	public Slot(LocalDate date) {
		this.date = date;
	}

	/** Marks this day as booked by the given user. */
	public void holdFor(User user) {
		this.status = SlotStatus.HELD;
		this.bookedBy = user;
	}

	public Long getId() { return id; }
	public LocalDate getDate() { return date; }
	public SlotStatus getStatus() { return status; }
	public User getBookedBy() { return bookedBy; }
}
