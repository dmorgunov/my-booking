package dm.dev.booking.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UnitRequest(
        UUID userId,
        int numberOfRooms,
        String accommodationType,
        int floor,
        BigDecimal baseCost,
        String description
) {
}
