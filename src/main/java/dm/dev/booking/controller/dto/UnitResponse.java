package dm.dev.booking.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UnitResponse(
        UUID id,
        UUID userId,
        int numberOfRooms,
        String accommodationType,
        int floor,
        BigDecimal baseCost,
        String description
) {
}