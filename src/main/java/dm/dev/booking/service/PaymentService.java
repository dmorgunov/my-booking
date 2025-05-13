package dm.dev.booking.service;

import dm.dev.booking.controller.dto.PaymentRequest;
import dm.dev.booking.model.Payment;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public Payment createPayment(PaymentRequest request) {
        var booking = bookingRepository.findById(request.bookingId()).orElseThrow();
        var payment = new Payment()
                .booking(booking)
                .paid(request.paid());
        return paymentRepository.save(payment);
    }

    public Optional<Payment> findByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
