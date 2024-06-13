package uns.ac.rs.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uns.ac.rs.entity.ReservationEvent;

import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class ReservationEventRepository implements PanacheMongoRepository<ReservationEvent> {

    // find by reservationId
    public Optional<ReservationEvent> findByReservationId(String reservationId) {
        return Optional.ofNullable(find("reservationId", reservationId).firstResult());
    }

    public Optional<ReservationEvent> findByIdOptional(String reservationId) {
        return Optional.ofNullable(findById(reservationId));
    }

    private ReservationEvent findById(String reservationId) {
        return find("reservationId", reservationId).firstResult();
    }

    public boolean canLeaveReviewOnHost(String guestUsername, String hostUsername) {
        return count("guestUsername = ?1 and hostUsername = ?2 and eventDate < ?3", guestUsername, hostUsername, LocalDate.now()) > 0;
    }

    public boolean canLeaveReviewOnAccommodation(String guestUsername, Long accommodationId) {
        return count("guestUsername = ?1 and accommodationId = ?2 and eventDate < ?3", guestUsername, accommodationId, LocalDate.now()) > 0;
    }
}