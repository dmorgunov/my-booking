package dm.dev.booking.service;

import dm.dev.booking.config.AppProperties;
import dm.dev.booking.controller.dto.BookingRequest;
import dm.dev.booking.model.Booking;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {


    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final CacheService cacheService;
    private final AppProperties appProperties;

    @Transactional
    public Booking bookUnit(BookingRequest request) {
        log.info("Booking request received: {}", request);

        var unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + request.unitId()));
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        Booking booking = new Booking()
                .unit(unit)
                .user(user)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(Booking.BookingStatus.PENDING);

        bookingRepository.save(booking);
        log.info("Booking created: {}", booking);

        String redisKey = "booking:timeout:" + booking.id();
        redissonClient.getBucket(redisKey).set("PENDING", appProperties.getExpiration());
        log.debug("Set Redis key {} with 15-minute TTL", redisKey);

        cacheService.updateAvailableUnitCount();
        return booking;
    }

    @Transactional
    public void cancelUnpaidBooking(UUID bookingId) {
        log.info("Attempting to cancel unpaid booking: {}", bookingId);

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.status() == Booking.BookingStatus.PENDING) {
                booking.status(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                cacheService.updateAvailableUnitCount();
                log.info("Cancelled unpaid booking: {}", booking);
            } else {
                log.debug("Booking {} not in PENDING state, skipping cancel", bookingId);
            }
        });
    }

    @Transactional
    public void confirmPayment(UUID bookingId) {
        log.info("Confirming payment for booking: {}", bookingId);

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.status(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Payment confirmed for booking: {}", booking);
        });
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        log.info("Cancelling booking: {}", bookingId);

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.status(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            cacheService.updateAvailableUnitCount();
            log.info("Booking cancelled: {}", booking);
        });
    }
}
