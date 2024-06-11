package uns.ac.rs.controlller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import uns.ac.rs.entity.Review;
import uns.ac.rs.service.ReviewService;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;


import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.vertx.core.json.JsonObject;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewController {

    @Inject
    ReviewService ReviewService;

    @Inject
    @Channel("filter-request-queue")
    Emitter<String> stringEmitter;


    @GET
    @PermitAll
    public List<Review> getAll() {
        System.out.println("Dobavi mi sve korisnike");
        stringEmitter.send("dobavi");
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
        Optional<Review> Review = ReviewService.getById(id);
        return Review.map(value -> Response.ok(value).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    // get review by target id and type
    @GET
    @Path("/target/{targetType}/{targetId}")
    @PermitAll
    public List<Review> getByTarget(@PathParam("targetType") Review.ReviewType targetType, @PathParam("targetId") String targetId) {
        return ReviewService.getByTarget(targetType, targetId);
    }


    @POST
    @RolesAllowed({"GUEST"})
    public Response addReview(Review Review) {
        ReviewService.addReview(Review);
        return Response.status(Response.Status.CREATED).entity(Review).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "GUEST" })
    public Response updateReview(@PathParam("id") UUID id, Review Review) {
        ReviewService.updateReview(id, Review);
        return Response.ok(Review).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "GUEST" })
    public Response deleteReview(@PathParam("id") UUID id) {
        ReviewService.deleteReview(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    
}
