package uns.ac.rs.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import uns.ac.rs.controlller.dto.ReviewDTO;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection="reviews")
public class Review extends PanacheMongoEntityBase {

    @BsonId
    private UUID id;

    private ReviewType targetType;
    private String reviewerUsername;
    private String hostUsername;
    private Long accommodationId;
    private int stars;
    private LocalDate reviewDate;

    public Review(ReviewDTO reviewDTO) {
        this.targetType = reviewDTO.getTargetType();
        this.hostUsername = reviewDTO.getHostUsername();
        this.accommodationId = reviewDTO.getAccommodationId();
        this.stars = reviewDTO.getStars();
    }

    public enum ReviewType {
        HOST, ACCOMMODATION
    }

}
