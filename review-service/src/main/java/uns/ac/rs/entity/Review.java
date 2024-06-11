package uns.ac.rs.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection="reviews")
public class Review extends PanacheMongoEntityBase {

    @BsonId
    public UUID id;

    public ReviewType targetType;
    public String reviewerUsername;
    public String targetId;
    public int stars;

    public enum ReviewType {
        HOST, ACCOMMODATION
    }

}
