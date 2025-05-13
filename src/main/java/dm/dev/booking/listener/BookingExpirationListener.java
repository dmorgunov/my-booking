package dm.dev.booking.listener;


import dm.dev.booking.service.BookingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingExpirationListener {

    private final RedissonClient redissonClient;
    private final BookingService bookingService;


    @PostConstruct
    public void listenForExpiredBookings() {
        redissonClient.getTopic("__keyevent@0__:expired")
                .addListener(String.class, (channel, expiredKey) -> {
                    if (expiredKey.startsWith("booking:timeout:")) {
                        String id = expiredKey.replace("booking:timeout:", "");
                        try {
                            UUID bookingId = UUID.fromString(id);
                            bookingService.cancelUnpaidBooking(bookingId);
                        } catch (IllegalArgumentException ignored) {
                            // skip malformed UUID keys
                        }
                    }
                });
    }
}