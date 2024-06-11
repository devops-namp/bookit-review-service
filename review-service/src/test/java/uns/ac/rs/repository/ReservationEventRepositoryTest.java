package uns.ac.rs.repository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.resources.MongoDBResource;

import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MongoDBResource.class)
public class ReservationEventRepositoryTest {

    @Inject
    ReservationEventRepository reservationEventRepository;



    @BeforeEach
    public void setUp() {
//        reservationEventRepository.deleteAll();
        reservationEventRepository.persist(new ReservationEvent());
        reservationEventRepository.deleteAll();
    }


    @Test
    @Transactional
    public void testFindByIdOptional() {
        ReservationEvent reservationEvent = new ReservationEvent();
        String reservationId = "reservationId";
        reservationEvent.setReservationId(reservationId);
        reservationEvent.setId(UUID.randomUUID().toString());
        reservationEventRepository.persist(reservationEvent);

        Optional<ReservationEvent> foundEvent = reservationEventRepository.findByReservationId(reservationId);
        assertTrue(foundEvent.isPresent());
        assertEquals(reservationEvent, foundEvent.get());
    }

    @Test
    @Transactional
    public void testCanLeaveReview() {
        ReservationEvent reservationEvent = new ReservationEvent();
        reservationEvent.setGuestUsername("guest");
        reservationEvent.setHostUsername("host");
        reservationEvent.setEventDate(LocalDate.now().minusDays(1));
        reservationEventRepository.persist(reservationEvent);

        boolean canLeaveReview = reservationEventRepository.canLeaveReview("guest", "host");
        assertTrue(canLeaveReview);
    }

    @Test
    @Transactional
    public void testCanLeaveReviewCant() {
        ReservationEvent reservationEvent = new ReservationEvent();
        reservationEvent.setGuestUsername("guest");
        reservationEvent.setHostUsername("host");
        reservationEvent.setEventDate(LocalDate.now().plusDays(1));
        reservationEventRepository.persist(reservationEvent);

        boolean canLeaveReview = reservationEventRepository.canLeaveReview("guest", "host");
        assertFalse(canLeaveReview);
    }

    @Test
    @Transactional
    public void testCanLeaveReviewNoEvent() {
        boolean canLeaveReview = reservationEventRepository.canLeaveReview("guest", "host");
        assertFalse(canLeaveReview);
    }


    @Test
    @Transactional
    public void testPersistAndRetrieve() {
        ReservationEvent reservationEvent = new ReservationEvent();
        reservationEvent.setReservationId("reservation1");
        reservationEvent.setGuestUsername("guest1");
        reservationEvent.setHostUsername("host1");
        reservationEvent.setEventDate(LocalDate.now().minusDays(5));
        reservationEvent.setId(UUID.randomUUID().toString());
        reservationEventRepository.persist(reservationEvent);

        Optional<ReservationEvent> foundEvent = reservationEventRepository.findByReservationId("reservation1");
        assertTrue(foundEvent.isPresent());
        assertEquals("guest1", foundEvent.get().getGuestUsername());
        assertEquals("host1", foundEvent.get().getHostUsername());
        assertEquals(LocalDate.now().minusDays(5), foundEvent.get().getEventDate());
    }

    @Test
    @Transactional
    public void testDelete() {
        ReservationEvent reservationEvent = new ReservationEvent();
        String reservationId = "reservationId";
        reservationEvent.setReservationId(reservationId);
        reservationEvent.setId(UUID.randomUUID().toString()); // Ensure _id is set
        reservationEventRepository.persist(reservationEvent);

        // now fetch it
        Optional<ReservationEvent> foundEvent = reservationEventRepository.findByIdOptional(reservationId);
        assertTrue(foundEvent.isPresent());

        // now delete it
        reservationEventRepository.delete(reservationEvent);

        // now try to fetch it again
        foundEvent = reservationEventRepository.findByIdOptional(reservationId);
        assertFalse(foundEvent.isPresent());
    }

}
