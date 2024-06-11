package uns.ac.rs.controlller;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import uns.ac.rs.entity.ReservationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import uns.ac.rs.service.ReviewService;


@ApplicationScoped
public class ReservationListener {

    @Inject
    ReviewService reviewService;

    @Incoming("reservation-made")
    public void onReservationMade(JsonObject reservationJson) {
        ReservationEvent reservation = reservationJson.mapTo(ReservationEvent.class);
        System.out.println("Reservation made for user: " + reservation.getGuestUsername());
        reviewService.saveReservationEvent(reservation);
    }

    @Incoming("reservation-canceled")
    public void onReservationCanceled(JsonObject reservationJson) {
        ReservationEvent reservation = reservationJson.mapTo(ReservationEvent.class);
        System.out.println("Reservation canceled for user: " + reservation.getGuestUsername());
        reviewService.removeReservationEvent(reservation.getReservationId());
    }

    @Incoming("test-queue")
    public void onTestQueue(String message) {
        System.out.println("Received message: " + message);
    }
}
