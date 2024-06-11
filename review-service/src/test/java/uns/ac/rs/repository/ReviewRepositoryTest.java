package uns.ac.rs.repository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import uns.ac.rs.entity.Review;
import uns.ac.rs.resources.MongoDBResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MongoDBResource.class)
public class ReviewRepositoryTest {

    @Inject
    ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {
        reviewRepository.persist(new Review());
        reviewRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testFindByIdOptional() {
        Review review = new Review(
                UUID.randomUUID(),
                Review.ReviewType.HOST,
                "reviewerUsername",
                "targetId",
                5
        );

        reviewRepository.persist(review);
        System.out.println("Review persisted with ID: " + review.getId());

        // get them all
        List<Review> reviews = reviewRepository.listAll();
        reviews.forEach(r -> System.out.println("Review: " + r));

        Optional<Review> foundReview = reviewRepository.findByIdOptional(review.getId());
        System.out.println("foundReview: " + foundReview);

        assertTrue(foundReview.isPresent(), "Review not found in the database");
        assertEquals(review, foundReview.get());
    }



    @Test
    @Transactional
    public void testFindByTarget() {
        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setTargetType(Review.ReviewType.HOST);
        review.setTargetId("targetId");
        reviewRepository.persist(review);

        List<Review> reviews = reviewRepository.findByTarget(Review.ReviewType.HOST, "targetId");
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(review, reviews.get(0));
    }

    @Test
    @Transactional
    public void testListAll() {
        Review review1 = new Review();
        review1.setId(UUID.randomUUID());
        reviewRepository.persist(review1);

        Review review2 = new Review();
        review2.setId(UUID.randomUUID());
        reviewRepository.persist(review2);

        List<Review> reviews = reviewRepository.listAll();
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    @Transactional
    public void testDelete() {
        Review review = new Review();
        UUID id = UUID.randomUUID();
        review.setId(id);
        reviewRepository.persist(review);

        reviewRepository.delete(review);
        Optional<Review> foundReview = reviewRepository.findByIdOptional(id);
        assertFalse(foundReview.isPresent());
    }
}

