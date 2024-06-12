package uns.ac.rs;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.repository.ReservationEventRepository;
import uns.ac.rs.repository.ReviewRepository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = Logger.getLogger("ListenerBean");

    @Inject
    ReservationEventRepository reservationEventRepository;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
        ReservationEvent reservationEvent = new ReservationEvent();
        reservationEvent.setId(UUID.randomUUID().toString());
        reservationEvent.setAccommodationId(1L);
        reservationEvent.setHostUsername("host1");
        reservationEvent.setReservationId("reservation1");
        reservationEvent.setGuestUsername("username1");
        reservationEvent.setEventType(ReservationEvent.EventType.CREATED.name());
        reservationEvent.setEventDate(LocalDate.now().minusWeeks(1));
        reservationEventRepository.persist(reservationEvent);
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}
