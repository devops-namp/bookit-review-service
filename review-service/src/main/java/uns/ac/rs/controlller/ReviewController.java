package uns.ac.rs.controlller;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import uns.ac.rs.controlller.dto.ReviewDTO;
import uns.ac.rs.entity.Review;
import uns.ac.rs.service.ReviewService;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;


import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.vertx.core.json.JsonObject;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewController {

    private static final Logger LOG = Logger.getLogger(String.valueOf(ReviewController.class));

    @Inject
    ReviewService ReviewService;

    @Inject
    SecurityIdentity identity;

    @Inject
    @Channel("filter-request-queue")
    Emitter<String> stringEmitter;


    @GET
    @PermitAll
    public List<Review> getAll() {
        LOG.info("Getting all reviews");
        return ReviewService.getAll();
    }

    @Incoming("filter-response-queue")
    public void consume(JsonObject json) {
        Book book = json.mapTo(Book.class);
        System.out.println("Primljena knjiga " + book.title + " by " + book.author);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") UUID id) {
        LOG.info("Getting review by id: " + id);
        Optional<Review> Review = ReviewService.getById(id);
        LOG.info("Review: " + Review);
        return Review.map(value -> Response.ok(value).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/target/{targetType}/{targetId}")
    @PermitAll
    public List<Review> getByTarget(@PathParam("targetType") Review.ReviewType targetType, @PathParam("targetId") String targetId) {
        LOG.info("Getting reviews by target: " + targetType + " " + targetId);
        return ReviewService.getByTarget(targetType, targetId);
    }


    @POST
    @RolesAllowed({"GUEST"})
    public Response addReview(@Valid ReviewDTO ReviewDTO) {
        LOG.info("Adding new review");
        Review Review = new Review(ReviewDTO);
        Review.setReviewDate(LocalDate.now());
        Review.setId(UUID.randomUUID());
        Review.setReviewerUsername(identity.getPrincipal().getName());
        LOG.info("Review: " + Review);
        ReviewService.addReview(Review);
        LOG.info("Review added");
        return Response.status(Response.Status.CREATED).entity(Review).build();
    }


    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "GUEST" })
    public Response deleteReview(@PathParam("id") UUID id) {
        String username = identity.getPrincipal().getName();
        LOG.info("Deleting review by id: " + id + " and username: " + username);
        ReviewService.deleteReview(id, username);
        LOG.info("Review deleted");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/health")
    @PermitAll
    public Response health() {
        return Response.ok().build();
    }

}
