package dm.dev.booking.controller;

import dm.dev.booking.controller.dto.PaymentRequest;
import dm.dev.booking.controller.dto.PaymentResponse;
import dm.dev.booking.model.Payment;
import dm.dev.booking.service.BookingService;
import dm.dev.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save")
    public ResponseEntity<PaymentResponse> savePayment(@RequestBody PaymentRequest request) {
        var payment = paymentService.createPayment(request);
        return ResponseEntity.ok(
                new PaymentResponse(payment.id(), payment.booking().id(), payment.paid()));
    }

}
