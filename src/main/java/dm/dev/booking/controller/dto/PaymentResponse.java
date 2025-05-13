package dm.dev.booking.controller.dto;

import java.util.UUID;

public record PaymentResponse(UUID id, UUID bookingId, boolean paid) {
}