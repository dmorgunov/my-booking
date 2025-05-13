package dm.dev.booking.controller;

import dm.dev.booking.controller.dto.BookingRequest;
import dm.dev.booking.controller.dto.BookingResponse;
import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Payment;
import dm.dev.booking.service.BookingService;
import dm.dev.booking.service.CacheService;
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
    private final CacheService cacheService;

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

    @GetMapping("/{id}/payment")
    public ResponseEntity<Payment> getPayment(@PathVariable UUID id) {
        return paymentService.findByBookingId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available-units")
    public ResponseEntity<Integer> getAvailableUnitCount() {
        return ResponseEntity.ok(cacheService.getAvailableUnitCount());
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