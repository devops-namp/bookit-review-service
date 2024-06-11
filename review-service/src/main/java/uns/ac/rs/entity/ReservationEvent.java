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
    public String id;
    public Long accommodationId;
    public String hostUsername;
    public String reservationId;
    public String guestUsername;
    public String eventType;
    public LocalDate eventDate;

    public enum EventType {
        CANCELLED, CREATED
    }
}
