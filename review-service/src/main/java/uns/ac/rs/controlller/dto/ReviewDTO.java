package uns.ac.rs.controlller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uns.ac.rs.entity.Review;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Review.ReviewType targetType;
    @NotNull
    @NotBlank
    public String hostUsername;
    @NotNull
    public Long accommodationId;
    @NotNull
    private int stars;
    private String id;
}
