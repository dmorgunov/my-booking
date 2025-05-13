package dm.dev.booking.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BookingResponse(UUID id, UUID unitId, UUID userId, LocalDate startDate, LocalDate endDate,
                              String status) {
}
