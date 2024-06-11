package uns.ac.rs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import uns.ac.rs.entity.ReservationEvent;
import uns.ac.rs.entity.Review;
import uns.ac.rs.repository.ReviewRepository;
import uns.ac.rs.resources.MongoDBResource;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MongoDBResource.class)
public class ReviewControllerTest {

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management")
            .withExposedPorts(5672, 15672);

    static {
        rabbitMQContainer.start();
        String host = rabbitMQContainer.getHost();
        Integer port = rabbitMQContainer.getMappedPort(5672);
        System.out.println("RabbitMQ Host: " + host);
        System.out.println("RabbitMQ Port: " + port);

        System.setProperty("rabbitmq.host", host);
        System.setProperty("rabbitmq.port", port.toString());
    }


    @Inject
    ReviewRepository reviewRepository;


    UUID fakeId = UUID.randomUUID();
    UUID fakeId2 = UUID.randomUUID();

    @BeforeEach
    public void initTestData() {
        reviewRepository.deleteAll();

        Review review1 = new Review();
        review1.id = fakeId;
        review1.targetType = Review.ReviewType.HOST;
        review1.targetId = "host1";
        review1.stars = 5;

        Review review2 = new Review();
        review2.id = fakeId2;
        review2.targetType = Review.ReviewType.ACCOMMODATION;
        review2.targetId = "acc1";
        review2.stars = 4;

        reviewRepository.persist(review1, review2);
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
    public void testAddReview() {
        Review newReview = new Review();
        newReview.targetType = Review.ReviewType.HOST;
        newReview.targetId = "someHostId";
        newReview.stars = 5;

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newReview)
                .when().post("/review")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testUpdateReview() {
        Review updateReview = new Review();
        updateReview.targetType = Review.ReviewType.ACCOMMODATION;
        updateReview.targetId = "someAccId";
        updateReview.stars = 4;

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateReview)
                .pathParam("id", fakeId)
                .when().put("/review/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testUpdateNonExistingReview() {
        Review updateReview = new Review();
        updateReview.targetType = Review.ReviewType.ACCOMMODATION;
        updateReview.targetId = "someAccId";
        updateReview.stars = 4;

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateReview)
                .pathParam("id", UUID.randomUUID())
                .when().put("/review/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteReview() {
        RestAssured.given()
                .pathParam("id", fakeId)
                .when().delete("/review/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
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
                .body("[0].targetId", equalTo("host1"))
                .body("[0].stars", equalTo(5));

        RestAssured.given()
                .pathParam("targetType", "ACCOMMODATION")
                .pathParam("targetId", "acc1")
                .when()
                .get("/review/target/{targetType}/{targetId}")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].targetId", equalTo("acc1"))
                .body("[0].stars", equalTo(4));
    }

    @Test
    public void testSendMessage() throws Exception {
        ReservationEvent event = new ReservationEvent();
        event.accommodationId = 1L;
        event.hostUsername = "host1";
        event.reservationId = "res1";
        event.guestUsername = "guest";
        event.eventType = ReservationEvent.EventType.CREATED.name();

        sendEvent(event, "reservation-made-queue");
    }

    public void sendEvent(ReservationEvent event, String queueName) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQContainer.getHost());
        factory.setPort(rabbitMQContainer.getMappedPort(5672));

        ObjectMapper mapper = new ObjectMapper();
        byte[] messageBodyBytes = mapper.writeValueAsBytes(event);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicPublish("", queueName, null, messageBodyBytes);
            System.out.println("Sent: " + new String(messageBodyBytes));
        }
    }


    @Test
    public void testSendTestMessage() throws Exception {
        String event = "eventttaadsadsad";

        ConnectionFactory factory = new ConnectionFactory();
        System.out.println(rabbitMQContainer.getHost() + " " + rabbitMQContainer.getMappedPort(5672));
        factory.setHost(rabbitMQContainer.getHost());
        factory.setPort(rabbitMQContainer.getMappedPort(5672));

        ObjectMapper mapper = new ObjectMapper();
        byte[] messageBodyBytes = mapper.writeValueAsBytes(event);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare("test-queue", false, false, false, null);

            channel.basicPublish("", "test-queue", null, messageBodyBytes);
            System.out.println("Sent: " + new String(messageBodyBytes));
        }
    }


}
