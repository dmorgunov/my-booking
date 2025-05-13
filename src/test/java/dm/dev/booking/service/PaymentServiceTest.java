package dm.dev.booking.service;

import dm.dev.booking.controller.dto.PaymentRequest;
import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Payment;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_shouldSavePaymentWithBooking() {
        UUID bookingId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(bookingId, true);
        Booking booking = new Booking().id(bookingId);
        Payment payment = new Payment().booking(booking).paid(true);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any())).thenReturn(payment);

        Payment result = paymentService.createPayment(request);

        assertEquals(bookingId, result.booking().id());
        assertEquals(true, result.paid());
        verify(paymentRepository).save(any());
    }

    @Test
    void findByBookingId_shouldReturnPayment() {
        UUID bookingId = UUID.randomUUID();
        Payment payment = new Payment().id(UUID.randomUUID()).paid(true);

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findByBookingId(bookingId);

        assertEquals(true, result.get().paid());
        verify(paymentRepository).findByBookingId(bookingId);
    }
}