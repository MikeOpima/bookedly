package io.github.mikeopima.bookedly.booking;

import io.github.mikeopima.bookedly.user.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlotRepository extends JpaRepository<Slot, Long> {

	List<Slot> findByStatusAndDateGreaterThanEqualOrderByDateAsc(SlotStatus status, LocalDate from);

	boolean existsByDate(LocalDate date);

	/**
	 * Books the slot only if it is still open and not in the past, in one SQL statement.
	 * If two people try to book the same day at the same moment, only one update matches,
	 * so the day can never be double-booked. Returns 1 if it worked, 0 if not.
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update Slot s
			   set s.status = :held, s.bookedBy = :user
			 where s.id = :id and s.status = :open and s.date >= :today""")
	int holdIfOpen(@Param("id") Long id, @Param("user") User user, @Param("today") LocalDate today,
			@Param("open") SlotStatus open, @Param("held") SlotStatus held);
}
