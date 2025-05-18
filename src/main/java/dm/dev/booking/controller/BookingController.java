package dm.dev.booking.controller;

import dm.dev.booking.controller.dto.BookingRequest;
import dm.dev.booking.controller.dto.BookingResponse;
import dm.dev.booking.controller.dto.PaymentResponse;
import dm.dev.booking.model.Booking;
import dm.dev.booking.service.BookingService;
import dm.dev.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> book(@RequestBody BookingRequest request) {
        var booking = bookingService.bookUnit(request);
        return ResponseEntity.ok(mapToResponse(booking));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmPayment(@PathVariable UUID id) {
        bookingService.confirmPayment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    //todo: get rid of custom mapping (mapstruct or similar)
    @GetMapping("/{id}/payment")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return paymentService.findByBookingId(id)
                .map(payment -> ResponseEntity.ok(
                        new PaymentResponse(payment.id(), payment.booking().id(), payment.paid())))
                .orElse(ResponseEntity.notFound().build());
    }

    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.id(),
                booking.unit().id(),
                booking.user().id(),
                booking.startDate(),
                booking.endDate(),
                booking.status().name()
        );
    }

}