package dm.dev.booking.controller.dto;

import java.util.UUID;

public record PaymentRequest(UUID bookingId, boolean paid) {
}
