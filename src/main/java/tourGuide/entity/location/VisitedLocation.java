package tourGuide.entity.location;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class VisitedLocation {
    public UUID userId;
    public Location location;
    public Date timeVisited;

    public VisitedLocation() {
    }

    public VisitedLocation(UUID userId, Location location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }
}
