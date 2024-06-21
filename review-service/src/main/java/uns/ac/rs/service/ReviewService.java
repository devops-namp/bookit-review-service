package uns.ac.rs.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import uns.ac.rs.controlller.exception.CantLeaveReview;
import uns.ac.rs.controlller.exception.ReviewNotFoundException;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.entity.Review;
import uns.ac.rs.event.NotificationEvent;
import uns.ac.rs.event.NotificationType;
import uns.ac.rs.repository.ReviewRepository;
import uns.ac.rs.repository.ReservationEventRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class ReviewService {

    @Inject
    @Channel("notification-queue")
    Emitter<NotificationEvent> eventEmitter;

    private static final Logger LOG = Logger.getLogger(String.valueOf(ReviewService.class));

    @Inject
    ReviewRepository ReviewRepository;

    @Inject
    ReservationEventRepository ReservationEventRepository;


    public List<Review> getAll() {
        return ReviewRepository.listAll();
    }

    public Optional<Review> getById(UUID id) {
        return ReviewRepository.findByIdOptional(id);
    }

    public Review addReview(Review review) {
        if (ReviewRepository.findByIdOptional(review.getId()).isPresent()) {
            LOG.info("Updating review: " + review);
            updateReview(review.getId(), review);
        } else {
            LOG.info("Saving review: " + review);
            ReviewRepository.persist(review);
        }
        LOG.info("Review saved");
        NotificationType notificationType = review.getTargetType().equals(Review.ReviewType.HOST) ? NotificationType.PERSONAL_REVIEW : NotificationType.ACCOMMODATION_REVIEW;
        NotificationEvent notificationEvent = new NotificationEvent("You have a new review!", review.getHostUsername(), notificationType);
        eventEmitter.send(notificationEvent);
        return review;
    }



    public void updateReview(UUID id, Review updatedReview) {
        Review existingReview = ReviewRepository.findByIdOptional(id)
                .orElseThrow(ReviewNotFoundException::new);

        existingReview.setStars(updatedReview.getStars());
        ReviewRepository.update(existingReview);
    }


    public void deleteReview(UUID id, String username) {
        if (!ReviewRepository.findByIdOptional(id).map(review -> review.getReviewerUsername().equals(username)).orElse(false)) {
            throw new ReviewNotFoundException();
        }
        ReviewRepository.delete(
                ReviewRepository.findByIdOptional(id).orElseThrow(ReviewNotFoundException::new)
        );
    }

    public List<Review> getByTarget(Review.ReviewType targetType, String targetId) {
        if (targetType == Review.ReviewType.HOST) {
            return ReviewRepository.findByHost(targetType, targetId);
        } else {
            return ReviewRepository.findByAccommodation(targetType, Long.parseLong(targetId));
        }
    }

    public void saveReservationEvent(ReservationEvent reservationEvent) {
        ReservationEventRepository.persist(reservationEvent);
    }

    public void removeReservationEvent(String reservationId) {
        ReservationEventRepository.findByIdOptional(reservationId).ifPresent(ReservationEventRepository::delete);
    }

    public boolean canLeaveReviewOnHost(String guestUsername, String hostUsername) {
        if (guestUsername == null || guestUsername.isBlank() || hostUsername == null || hostUsername.isBlank()) {
            return false;
        }
        return ReservationEventRepository.canLeaveReviewOnHost(guestUsername, hostUsername);
    }

}
