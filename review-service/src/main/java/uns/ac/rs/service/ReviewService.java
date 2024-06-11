package uns.ac.rs.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uns.ac.rs.controlller.exception.ReviewNotFoundException;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.entity.Review;
import uns.ac.rs.repository.ReviewRepository;
import uns.ac.rs.repository.ReservationEventRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ReviewService {

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

    public void addReview(Review Review) {
        ReviewRepository.persist(Review);
    }

    public void updateReview(UUID id, Review updatedReview) {
        Review existingReview = ReviewRepository.findByIdOptional(id)
                .orElseThrow(ReviewNotFoundException::new);

        existingReview.setStars(updatedReview.getStars());
        ReviewRepository.update(existingReview);
    }


    public void deleteReview(UUID id) {
        ReviewRepository.delete(
                ReviewRepository.findByIdOptional(id).orElseThrow(ReviewNotFoundException::new)
        );
    }

    public List<Review> getByTarget(Review.ReviewType targetType, String targetId) {
        return ReviewRepository.findByTarget(targetType, targetId);
    }

    public void saveReservationEvent(ReservationEvent reservationEvent) {
        ReservationEventRepository.persist(reservationEvent);
    }

    public void removeReservationEvent(String reservationId) {
        ReservationEventRepository.findByIdOptional(reservationId).ifPresent(ReservationEventRepository::delete);
    }

    public boolean canLeaveReview(String guestUsername, String hostUsername) {
        return ReservationEventRepository.canLeaveReview(guestUsername, hostUsername);
    }

}
