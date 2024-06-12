package uns.ac.rs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import uns.ac.rs.controlller.dto.ReviewDTO;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.entity.Review;
import uns.ac.rs.repository.ReservationEventRepository;
import uns.ac.rs.repository.ReviewRepository;
import uns.ac.rs.resources.MongoDBResource;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MongoDBResource.class)
public class ReviewControllerTest {
//
//    @Container
//    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management")
//            .withExposedPorts(5672, 15672);
//
//    static {
//        rabbitMQContainer.start();
//        String host = rabbitMQContainer.getHost();
//        Integer port = rabbitMQContainer.getMappedPort(5672);
//        System.out.println("RabbitMQ Host: " + host);
//        System.out.println("RabbitMQ Port: " + port);
//
//        System.setProperty("rabbitmq.host", host);
//        System.setProperty("rabbitmq.port", port.toString());
//    }


    @Inject
    ReviewRepository reviewRepository;

    @Inject
    ReservationEventRepository reservationEventRepository;


    UUID fakeId = UUID.randomUUID();
    UUID fakeId2 = UUID.randomUUID();

    @BeforeEach
    public void initTestData() {
        reviewRepository.deleteAll();
        reservationEventRepository.deleteAll();

        Review review1 = new Review();
        review1.setId(fakeId);
        review1.setTargetType(Review.ReviewType.HOST);
        review1.setHostUsername("host1");
        review1.setReviewerUsername("reviewer1");
        review1.setAccommodationId(1L);
        review1.setStars(5);

        Review review2 = new Review();
        review2.setId(fakeId2);
        review2.setHostUsername("host2");
        review2.setReviewerUsername("reviewer2");
        review2.setAccommodationId(2L);
        review2.setTargetType(Review.ReviewType.ACCOMMODATION);
        review2.setStars(4);
        reviewRepository.persist(review1, review2);

        ReservationEvent event = new ReservationEvent();
        event.setId(UUID.randomUUID().toString());
        event.setAccommodationId(1L);
        event.setHostUsername("host1");
        event.setReservationId("res1");
        event.setGuestUsername("reviewer1");
        event.setEventType(ReservationEvent.EventType.CREATED.name());
        event.setEventDate(LocalDate.now().minusWeeks(1));
        reservationEventRepository.persist(event);
    }

    @Test
    public void testGetById() {
        UUID fakeId = UUID.randomUUID();
        RestAssured.given()
                .pathParam("id", fakeId)
                .when().get("/review/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @TestSecurity(user = "reviewer1", roles = {"GUEST"})
    public void testAddReview() {

        // list all reservation events
        reservationEventRepository.findAll().list().forEach(System.out::println);

        ReviewDTO newReview = new ReviewDTO();
        newReview.setTargetType(Review.ReviewType.ACCOMMODATION);
        newReview.setHostUsername("host1");
        newReview.setAccommodationId(1L);
        newReview.setStars(4);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newReview)
                .when().post("/review")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "reviewer1", roles = {"GUEST"})
    public void testDeleteReview() {
        RestAssured.given()
                .pathParam("id", fakeId)
                .when().delete("/review/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @TestSecurity(user = "reviewer1", roles = {"GUEST"})
    public void testDeleteNonExistingReview() {
        RestAssured.given()
                .pathParam("id", UUID.randomUUID())
                .when().delete("/review/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetByTarget() {
        RestAssured.given()
                .pathParam("targetType", "HOST")
                .pathParam("targetId", "host1")
                .when()
                .get("/review/target/{targetType}/{targetId}")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].hostUsername", equalTo("host1"))
                .body("[0].stars", equalTo(5));

        RestAssured.given()
                .pathParam("targetType", "ACCOMMODATION")
                .pathParam("targetId", 2L)
                .when()
                .get("/review/target/{targetType}/{targetId}")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].accommodationId", equalTo(2))
                .body("[0].stars", equalTo(4));
    }

}
