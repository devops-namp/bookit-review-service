package uns.ac.rs.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.entity.Review;
import uns.ac.rs.repository.ReservationEventRepository;
import uns.ac.rs.repository.ReviewRepository;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ReviewServiceTest {

    @InjectMock
    ReviewRepository reviewRepository;

    @InjectMock
    ReservationEventRepository reservationEventRepository;

    @Inject
    ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAll() {
        when(reviewRepository.listAll()).thenReturn(List.of(new Review()));
        List<Review> reviews = reviewService.getAll();
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(reviewRepository).listAll();
    }

    @Test
    public void testGetById() {
        UUID id = UUID.randomUUID();
        Review review = new Review();
        when(reviewRepository.findByIdOptional(id)).thenReturn(Optional.of(review));

        Optional<Review> foundReview = reviewService.getById(id);
        assertTrue(foundReview.isPresent());
        assertEquals(review, foundReview.get());
        verify(reviewRepository).findByIdOptional(id);
    }

    @Test
    @Transactional
    public void testAddReview() {
        Review review = new Review();
        review.setReviewerUsername("reviewer");
        review.setHostUsername("host");
        review.setStars(5);
        review.setTargetType(Review.ReviewType.HOST);
        when(reservationEventRepository.canLeaveReview("reviewer", "host")).thenReturn(true);
        reviewService.addReview(review);
        verify(reviewRepository).persist(review);
    }

    @Test
    @Transactional
    public void testUpdateReview() {
        UUID id = UUID.randomUUID();
        Review existingReview = new Review();
        Review updatedReview = new Review();
        updatedReview.setStars(5);

        when(reviewRepository.findByIdOptional(id)).thenReturn(Optional.of(existingReview));

        reviewService.updateReview(id, updatedReview);
        assertEquals(5, existingReview.getStars());
        verify(reviewRepository).update(existingReview);
    }

    @Test
    @Transactional
    public void testDeleteReview() {
        UUID id = UUID.randomUUID();
        Review review = new Review();
        review.setReviewerUsername("username");

        when(reviewRepository.findByIdOptional(id)).thenReturn(Optional.of(review));

        reviewService.deleteReview(id, "username");
        verify(reviewRepository).delete(review);
    }

    @Test
    public void testGetByTarget() {
        Review.ReviewType targetType = Review.ReviewType.HOST;
        String targetId = "targetId";

        when(reviewRepository.findByHost(targetType, targetId)).thenReturn(List.of(new Review()));

        List<Review> reviews = reviewService.getByTarget(targetType, targetId);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(reviewRepository).findByHost(targetType, targetId);
    }

    @Test
    @Transactional
    public void testSaveReservationEvent() {
        ReservationEvent reservationEvent = new ReservationEvent();
        reviewService.saveReservationEvent(reservationEvent);
        verify(reservationEventRepository).persist(reservationEvent);
    }

    @Test
    @Transactional
    public void testRemoveReservationEvent() {
        String reservationId = "reservationId";
        ReservationEvent reservationEvent = new ReservationEvent();

        when(reservationEventRepository.findByIdOptional(reservationId)).thenReturn(Optional.of(reservationEvent));

        reviewService.removeReservationEvent(reservationId);
        verify(reservationEventRepository).delete(reservationEvent);
    }

    @Test
    @Transactional
    public void testCanLeaveReview() {
        String guestUsername = "guest";
        String hostUsername = "host";

        when(reservationEventRepository.canLeaveReview(guestUsername, hostUsername)).thenReturn(true);

        boolean result = reviewService.canLeaveReview(guestUsername, hostUsername);
        assertTrue(result);
        verify(reservationEventRepository).canLeaveReview(guestUsername, hostUsername);
    }
}