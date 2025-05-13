package dm.dev.booking.service;

import dm.dev.booking.controller.dto.BookingRequest;
import dm.dev.booking.model.Booking;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final CacheService cacheService;

    public Booking bookUnit(BookingRequest request) {
        var unit = unitRepository.findById(request.unitId()).orElseThrow();
        var user = userRepository.findById(request.userId()).orElseThrow();

        Booking booking = new Booking()
                .unit(unit)
                .user(user)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(Booking.BookingStatus.PENDING);

        bookingRepository.save(booking);
        String redisKey = "booking:timeout:" + booking.id();
        redissonClient.getBucket(redisKey).set("PENDING", Duration.ofMinutes(15));

        cacheService.updateAvailableUnitCount();
        return booking;
    }

    public void cancelUnpaidBooking(UUID bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.status() == Booking.BookingStatus.PENDING) {
                booking.status(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                cacheService.updateAvailableUnitCount();
            }
        });
    }

    public void confirmPayment(UUID bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.status(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        });
    }

    public void cancelBooking(UUID bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.status(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            cacheService.updateAvailableUnitCount();
        });
    }


}