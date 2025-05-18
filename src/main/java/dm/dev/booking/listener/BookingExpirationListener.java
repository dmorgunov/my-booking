package dm.dev.booking.listener;

import dm.dev.booking.service.BookingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpirationListener {

    private final RedissonClient redissonClient;
    private final BookingService bookingService;

    @PostConstruct
    public void listenForExpiredBookings() {
        log.info("BookingExpirationListener initialized. Listening for expired Redis keys...");

        redissonClient.getTopic("__keyevent@0__:expired")
                .addListener(String.class, (channel, expiredKey) -> {
                    log.debug("Received expired key event: {}", expiredKey);

                    if (expiredKey.startsWith("booking:timeout:")) {
                        String id = expiredKey.replace("booking:timeout:", "");
                        try {
                            UUID bookingId = UUID.fromString(id);
                            log.info("Booking expired. Cancelling unpaid booking: {}", bookingId);
                            bookingService.cancelUnpaidBooking(bookingId);
                        } catch (IllegalArgumentException ex) {
                            log.warn("Invalid booking ID in expired key: {} — skipping", expiredKey);
                        }
                    }
                });
    }
}
