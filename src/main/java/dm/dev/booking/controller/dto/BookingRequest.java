package dm.dev.booking.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BookingRequest(UUID unitId, UUID userId, LocalDate startDate, LocalDate endDate) {
}