package dm.dev.booking.service;

import dm.dev.booking.controller.dto.PaymentRequest;
import dm.dev.booking.model.Payment;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Payment createPayment(PaymentRequest request) {
        log.info("Creating payment for booking ID: {}", request.bookingId());

        var booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + request.bookingId()));

        var payment = new Payment()
                .booking(booking)
                .paid(request.paid());

        var saved = paymentRepository.save(payment);
        log.info("Payment saved: {}", saved);
        return saved;
    }

    @Transactional
    public Optional<Payment> findByBookingId(UUID bookingId) {
        log.debug("Looking for payment by booking ID: {}", bookingId);
        return paymentRepository.findByBookingId(bookingId);
    }
}
