package uns.ac.rs.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection="reservation-events")
public class ReservationEvent extends PanacheMongoEntityBase {
    @BsonId
    private String id;
    private Long accommodationId;
    private String hostUsername;
    private String reservationId;
    private String guestUsername;
    private String eventType;
    private LocalDate eventDate;

    public enum EventType {
        CANCELLED, CREATED
    }
}
